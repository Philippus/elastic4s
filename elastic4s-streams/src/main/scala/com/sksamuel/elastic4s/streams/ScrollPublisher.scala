package com.sksamuel.elastic4s.streams

import akka.actor.{Actor, ActorSystem, PoisonPill, Props, Stash}
import com.sksamuel.elastic4s.streams.PublishActor.Ready
import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl, RichSearchHit, SearchDefinition}
import org.elasticsearch.ElasticsearchException
import org.elasticsearch.action.search.SearchResponse
import org.reactivestreams.{Publisher, Subscriber, Subscription}

import scala.collection.mutable
import scala.util.{Failure, Success}

/**
 * An implementation of the reactive API Publisher, that publishes documents using an elasticsearch
 * scroll cursor. The initial query must be provided to the publisher, and there are helpers to create
 * a query for all documents in an index (and type).
 *
 * @param client a client for the cluster
 * @param search the initial search query to execute
 * @param elements the maximum number of elements to return
 * @param system an Actor system required by the publisher
 */
class ScrollPublisher(client: ElasticClient,
                      search: SearchDefinition,
                      elements: Long)
                     (implicit system: ActorSystem) extends Publisher[RichSearchHit] {

  override def subscribe(s: Subscriber[_ >: RichSearchHit]): Unit = {
    // Rule 1.9
    if (s == null) throw new NullPointerException("Rule 1.9: Subscriber cannot be null")
    val subscription = new ScrollSubscription(client, search, s, elements)
    s.onSubscribe(subscription)
    // rule 1.03 the subscription should not process onNext until the onSubscribe call has returned
    subscription.ready()
  }
}

class ScrollSubscription(client: ElasticClient, query: SearchDefinition, s: Subscriber[_ >: RichSearchHit], max: Long)
                        (implicit system: ActorSystem) extends Subscription {

  val actor = system.actorOf(Props(new PublishActor(client, query, s, max)))

  private[streams] def ready() : Unit = {
    actor ! Ready
  }

  override def cancel(): Unit = {
    // Rule 3.5: this call is idempotent, is fast, and thread safe
    // Rule 3.7: after cancelling, further calls should be no-ops, which is handled by the actor
    // we don't mind the subscriber having any pending requests before cancellation is processed
    actor ! PoisonPill
  }

  override def request(n: Long): Unit = {
    // spec 3.9
    if (n < 1) s.onError(new java.lang.IllegalArgumentException("Rule 3.9: Must request > 0 elements"))
    // Rule 3.4 this method returns quickly as the search request is non-blocking
    actor ! PublishActor.Request(n)
  }
}

object PublishActor {
  case object Ready
  case class Request(n: Long)
}

class PublishActor(client: ElasticClient,
                   query: SearchDefinition,
                   s: Subscriber[_ >: RichSearchHit],
                   max: Long) extends Actor with Stash {

  import ElasticDsl._
  import PublishActor._
  import context.dispatcher

  private var scrollId: String = null
  private var processed: Long = 0
  private val queue: mutable.Queue[RichSearchHit] = mutable.Queue.empty

  // Parse the keep alive setting out of the original query.
  private val keepAlive = Option(query.build.scroll).map(s => s.keepAlive).map(t => t.toString).getOrElse("1m")

  override def receive = {
    case Ready =>
      context become ready
      unstashAll()
    case _ =>
      stash()
  }

  def ready: Actor.Receive = {
    case Request(n) if n > 0 =>
      if (queue.isEmpty) {
        Option(scrollId) match {
          case None => client.execute(query).onComplete(result => self ! result)
          case Some(id) => client.execute(search scroll id keepAlive keepAlive).onComplete(result => self ! result)
        }
        context become fetching
        self ! Request(n)
      } else {
        s.onNext(queue.dequeue)
        processed = processed + 1
        if (processed == max) {
          s.onComplete()
          context.stop(self)
        } else {
          self ! Request(n - 1)
        }
      }
  }

  def fetching: Actor.Receive = {
    // while fetching (waiting on elasticsearch) we need to postpone requests until the results are back
    // we can do this handily by stashing them
    case Request(n) =>
      stash()
    case Success(resp: SearchResponse) if resp.isTimedOut =>
      s.onError(new ElasticsearchException("Request terminated early or timed out"))
      context.stop(self)
    // if we had no results from ES then we're done
    case Success(resp: SearchResponse) if resp.isEmpty =>
      s.onComplete()
      context.stop(self)
    // more results and we can unleash the beast (stashed requests)
    case Success(resp: SearchResponse) =>
      scrollId = resp.getScrollId
      queue.enqueue(resp.hits: _*)
      context become ready
      unstashAll()
    // if the request to elastic failed we will terminate the subscription sorry
    case Failure(t) =>
      s.onError(t)
      context.stop(self)
  }

}

package com.sksamuel.elastic4s.streams

import akka.actor.{Actor, ActorRefFactory, PoisonPill, Props, Stash}
import com.sksamuel.elastic4s.{ElasticClient, RequestFailure, RequestSuccess}
import com.sksamuel.elastic4s.requests.searches.{SearchHit, SearchRequest, SearchResponse}
import com.sksamuel.elastic4s.streams.PublishActor.Ready
import com.sksamuel.exts.Logging
import com.sksamuel.exts.OptionImplicits._
import org.reactivestreams.{Publisher, Subscriber, Subscription}

import scala.collection.mutable
import scala.util.{Failure, Success}

/**
  * An implementation of the reactive API Publisher, that publishes documents using an elasticsearch
  * scroll cursor. The initial query must be provided to the publisher, and there are helpers to create
  * a query for all documents in an index (and type).
  *
  * @param client          a client for the cluster
  * @param search          the initial search query to execute
  * @param maxItems        the maximum number of elements to return
  * @param actorRefFactory an Actor reference factory required by the publisher
  */
class ScrollPublisher private[streams] (client: ElasticClient, search: SearchRequest, maxItems: Long)(
  implicit actorRefFactory: ActorRefFactory
) extends Publisher[SearchHit] {
  require(search.keepAlive.isDefined, "Search Definition must have a scroll to be used as Publisher")

  override def subscribe(s: Subscriber[_ >: SearchHit]): Unit = {
    // Rule 1.9 subscriber cannot be null
    if (s == null) throw new NullPointerException("Rule 1.9: Subscriber cannot be null")
    val subscription = new ScrollSubscription(client, search, s, maxItems)
    s.onSubscribe(subscription)
    // rule 1.03 the subscription should not invoke any onNext's until the onSubscribe call has returned
    // even tho the user might call request in the onSubscribe, we can't start sending the results yet.
    // this ready method signals to the actor that its ok to start sending data.
    subscription.ready()
  }
}

class ScrollSubscription(client: ElasticClient, query: SearchRequest, s: Subscriber[_ >: SearchHit], max: Long)(
  implicit actorRefFactory: ActorRefFactory
) extends Subscription {

  private val actor = actorRefFactory.actorOf(Props(new PublishActor(client, query, s, max)))

  private[streams] def ready(): Unit =
    actor ! PublishActor.Ready

  override def cancel(): Unit =
    // Rule 3.5: this call is idempotent, is fast, and thread safe
    // Rule 3.7: after cancelling, further calls should be no-ops, which is handled by the actor
    // we don't mind the subscriber having any pending requests before cancellation is processed
    actor ! PoisonPill

  override def request(n: Long): Unit = {
    // Rule 3.9
    if (n < 1) s.onError(new java.lang.IllegalArgumentException("Rule 3.9: Must request > 0 elements"))
    // Rule 3.4 this method returns quickly as the search request is non-blocking
    actor ! PublishActor.Request(n)
  }
}

object PublishActor {
  object Ready
  case class Request(n: Long)
}

class PublishActor(client: ElasticClient, query: SearchRequest, s: Subscriber[_ >: SearchHit], max: Long)
    extends Actor
    with Stash
    with Logging {

  import com.sksamuel.elastic4s.ElasticDsl._
  import context.dispatcher

  private var scrollId: String                = _
  private var processed: Long                 = 0
  private val queue: mutable.Queue[SearchHit] = mutable.Queue.empty

  // Parse the keep alive setting out of the original query.
  private val keepAlive = query.keepAlive.map(_.toString).getOrElse("1m")

  // rule 1.03 the subscription should not send any results until the onSubscribe call has returned
  // even tho the user might call request in the onSubscribe, we can't start sending the results yet.
  // this ready method signals to the actor that its ok to start sending data. In the meantime we just stash requests.
  override def receive: PartialFunction[Any, Unit] = {
    case Ready =>
      context become ready
      logger.info("Scroll publisher has become 'Ready'")
      unstashAll()
    case _ =>
      stash()
  }

  private def send(k: Long): Unit = {
    require(queue.size >= k)
    for (_ <- 0l until k)
      if (max == 0 || processed < max) {
        s.onNext(queue.dequeue)
        processed = processed + 1
        if (processed == max && max > 0) {
          s.onComplete()
          context.stop(self)
        }
      }
  }

  // ready is the standard state, we can service requests and request more from upstream as well
  private def ready: Actor.Receive = {
    // if a request comes in for more than is currently available,
    // we will send a request for more while sending what we can now
    case PublishActor.Request(n) if n > queue.size =>
      val toRequest = n - queue.size
      logger.debug(
        s"Request for $n items, but only ${queue.size} available; sending ${queue.size} now, requesting $toRequest from upstream"
      )
      Option(scrollId) match {
        case None     => client.execute(query).onComplete(result => self ! result)
        case Some(id) => client.execute(searchScroll(id) keepAlive keepAlive).onComplete(result => self ! result)
      }
      // we switch state while we're waiting on elasticsearch, so we know not to send another request to ES
      // because we are using a scroll and can only have one active request at at time.
      context become fetching
      logger.info("Scroll publisher has become 'Fetching'")
      // queue up a new request to handle the remaining ones required when the ES response comes in
      self ! PublishActor.Request(toRequest)
      send(queue.size)
    // in this case, we have enough available so just send 'em
    case PublishActor.Request(n) =>
      logger.debug(s"Request for $n items; sending")
      send(n)
  }

  // fetching state is when we're waiting for a reply from es for a request we sent
  private def fetching: Actor.Receive = {
    // if we're in fetching mode, its because we ran out of results to send
    // so any requests must be stashed until a fresh batch arrives
    case PublishActor.Request(n) =>
      logger.debug(s"Request for $n items but we're already waiting on a response; stashing request")
      require(queue.isEmpty) // must be empty or why did we not send it before switching to this mode?
      stash()
    // if the request to elastic failed we will terminate the subscription
    case Failure(t) =>
      logger.warn("Elasticsearch returned a failure; will terminate the subscription", t)
      s.onError(t)
      context.stop(self)
    case Success(resp: RequestFailure) =>
      logger.warn("Request errored, will terminate the subscription", resp.error.toString)
      s.onError(new RuntimeException(resp.error.toString))
      context.stop(self)
    // handle when the es request times out
    case Success(resp: RequestSuccess[SearchResponse]) if resp.result.isTimedOut =>
      logger.warn("Elasticsearch request timed out; will terminate the subscription")
      s.onError(new RuntimeException("Request terminated early or timed out"))
      context.stop(self)
    // if we had no results from ES then we have nothing left to publish and our work here is done
    case Success(resp: RequestSuccess[SearchResponse]) if resp.result.isEmpty =>
      logger.debug("Response from ES came back empty; this means no more items upstream so will complete subscription")
      s.onComplete()
      client.execute(clearScroll(scrollId))
      logger.debug("Stopping publisher actor")
      context.stop(self)
    // more results and we can unleash the beast (stashed requests) and switch back to ready mode
    case Success(resp: RequestSuccess[SearchResponse]) =>
      scrollId = resp.result.scrollId.getOrError("Response did not include a scroll id")
      queue.enqueue(resp.result.hits.hits: _*)
      context become ready
      unstashAll()
  }
}

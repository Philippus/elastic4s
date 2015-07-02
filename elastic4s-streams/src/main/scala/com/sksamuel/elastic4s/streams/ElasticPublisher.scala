package com.sksamuel.elastic4s.streams

import java.util.concurrent.atomic.AtomicBoolean

import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl, RichSearchHit, RichSearchResponse, SearchDefinition}
import org.elasticsearch.search.SearchHit
import org.reactivestreams.{Publisher, Subscriber, Subscription}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ElasticPublisher(client: ElasticClient, search: SearchDefinition)
                      (implicit executor: ExecutionContext) extends Publisher[RichSearchHit] {

  import ElasticDsl._

  override def subscribe(s: Subscriber[_ >: RichSearchHit]): Unit = {
    // Rule 1.9
    if (s == null) throw new NullPointerException("Subscriber cannot be null")
    client.execute(search).onComplete {
      case Success(resp) => s.onSubscribe(new ElasticSubscription(client, resp, s))
      case Failure(t) => s.onError(t)
    }
  }
}

class ElasticSubscription(client: ElasticClient, resp: RichSearchResponse, s: Subscriber[_ >: RichSearchHit])
                         (implicit executor: ExecutionContext) extends Subscription {

  import ElasticDsl._

  private object NoMoreResults
  private var id = resp.scrollId
  private val active = new AtomicBoolean(true)
  // this is any because I'm using sentinel values to indicate state
  private val queue: mutable.Queue[Any] = mutable.Queue.empty
  // this future is used as a way of chaining requests to ensure that even on a multi-thread
  // executor, only one request is ever executing at any time.
  private var future: Future[Unit] = Future.successful()

  override def cancel(): Unit = {
    // Rule 3.5: this call is idempotent, is fast, and thread safe
    // Rule 3.7: after cancelling, further calls should be no-ops
    if (active.getAndSet(false)) {
      client.execute(clearScroll(id))
    }
  }

  override def request(n: Long): Unit = {
    // Rule 3.4 this method returns quickly as the search request is non-blocking
    // Rule 3.6: after cancelling, further calls should be no-ops
    if (active.get) {
      if (queue.isEmpty) {
        // we flatmap on this future so that this command won't execute until the previous one has finished
        future = future.flatMap { _ =>
          // we enqueue another batch of hits, possibly more than the user requested which is allowed
          // we set the future to be the result of the next scroll request, and by rule 2.7 we know
          // that the subscriber must only invoke request from the same thread.
          val future = client.execute {
            search scroll id
          }
          future.onFailure {
            case t => queue.enqueue(t) // enqeue so that the subscriber can get the rest before the error
          }
          future.map { resp =>
            if (resp.hits.isEmpty) {
              queue.enqueue(NoMoreResults) // we had no more hits
            } else {
              id = resp.getScrollId
              queue.enqueue(resp.hits: _*)
              request(n)
            }
          }
        }
      } else {
        queue.dequeue match {
          case t: Throwable =>
            s.onError(t)
            active.set(false)
          case NoMoreResults =>
            s.onComplete()
            active.set(false)
          case hit: RichSearchHit =>
            s.onNext(hit)
            request(n - 1)
        }
      }
    }
  }
}
package com.sksamuel.elastic4s.streams

import java.util.concurrent.atomic.AtomicBoolean

import com.sksamuel.elastic4s.{ElasticDsl, ElasticClient, RichSearchHit, RichSearchResponse, SearchDefinition}
import org.reactivestreams.{Publisher, Subscriber, Subscription}

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ElasticPublisher(client: ElasticClient, search: SearchDefinition)
                      (implicit executor: ExecutionContext)
  extends Publisher[RichSearchHit] {

  import ElasticDsl._

  override def subscribe(s: Subscriber[_ >: RichSearchHit]): Unit = {
    client.execute(search).onComplete {
      case Success(resp) => s.onSubscribe(new ElasticSubscription(client, resp, s))
      case Failure(t) => s.onError(t)
    }
  }
}

class ElasticSubscription(client: ElasticClient, resp: RichSearchResponse, s: Subscriber[_ >: RichSearchHit])
  extends Subscription {

  import ElasticDsl._

  private var id = resp.scrollId
  private val ended = new AtomicBoolean(false)
  private val queue: mutable.Queue[RichSearchHit] = mutable.Queue(resp.hits: _*)

  override def cancel(): Unit = {
    if (!ended.getAndSet(true)) {
      client.execute(clearScroll(id))
    }
  }

  override def request(n: Long): Unit = {
    if (!ended.get) {
      if (queue.isEmpty) {
        // need to request some more
        client.execute {
          search scroll id
        }.onComplete {
          case Success(resp) =>
            if (resp.hits.isEmpty) {
              s.onComplete()
              ended.set(true)
              // was no more results in this scroll
            } else {
              id = resp.scrollId
              queue.enqueue(resp.hits: _*)
              request(n)
            }
          case Failure(t) =>
            s.onError(t)
            ended.set(true)
        }
      } else {
        s.onNext(queue.dequeue)
        request(n - 1)
      }
    }
  }
}


package com.sksamuel.elastic4s.streams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl, IndexType, SearchDefinition}

import scala.language.implicitConversions

object ReactiveElastic {

  implicit class ReactiveElastic(client: ElasticClient) {

    import ElasticDsl._

    def subscriber[T](batchSize: Int = 100,
                      concurrentRequests: Int = 5,
                      listener: ResponseListener = ResponseListener.noop,
                      completionFn: () => Unit = () => (),
                      errorFn: Throwable => Unit = _ => ())
                     (implicit provider: RequestBuilder[T], system: ActorSystem): BulkIndexingSubscriber[T] = {
      new BulkIndexingSubscriber[T](client, provider, listener, batchSize, concurrentRequests, completionFn, errorFn)
    }

    def publisher(indexType: IndexType, elements: Long = Long.MaxValue, keepAlive: String = "1m")
                 (implicit system: ActorSystem): ScrollPublisher = {
      publisher(search in indexType query "*:*" scroll keepAlive)
    }

    def publisher(q: SearchDefinition)(implicit system: ActorSystem): ScrollPublisher = publisher(q, Long.MaxValue)
    def publisher(q: SearchDefinition, elements: Long)
                 (implicit system: ActorSystem): ScrollPublisher = {
      new ScrollPublisher(client, q, elements)
    }
  }
}

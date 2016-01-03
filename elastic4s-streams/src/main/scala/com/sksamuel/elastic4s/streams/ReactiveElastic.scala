package com.sksamuel.elastic4s.streams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl, IndexAndTypes, SearchDefinition}

import scala.concurrent.duration._
import scala.language.implicitConversions

object ReactiveElastic {

  implicit class ReactiveElastic(client: ElasticClient) {

    import ElasticDsl._

    def subscriber[T](batchSize: Int = 100,
                      concurrentRequests: Int = 5,
                      refreshAfterOp: Boolean = false,
                      listener: ResponseListener = ResponseListener.noop,
                      completionFn: () => Unit = () => (),
                      errorFn: Throwable => Unit = _ => (),
                      flushInterval: Option[FiniteDuration] = None,
                      flushAfter: Option[FiniteDuration] = None,
                      failureWait: FiniteDuration = 2.seconds,
                      maxAttempts: Int = 5)
                     (implicit builder: RequestBuilder[T], system: ActorSystem): BulkIndexingSubscriber[T] = {
      val config = SubscriberConfig(
        batchSize = batchSize,
        concurrentRequests = concurrentRequests,
        refreshAfterOp = refreshAfterOp,
        listener = listener,
        completionFn = completionFn,
        errorFn = errorFn,
        failureWait = failureWait,
        flushInterval = flushInterval,
        flushAfter = flushAfter,
        maxAttempts = maxAttempts
      )
      new BulkIndexingSubscriber[T](client, builder, config)
    }

    def publisher(indexType: IndexAndTypes, elements: Long = Long.MaxValue, keepAlive: String = "1m")
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

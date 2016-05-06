package com.sksamuel.elastic4s.streams

import akka.actor.ActorRefFactory
import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl, IndexAndTypes, SearchDefinition}

import scala.concurrent.duration._
import scala.language.implicitConversions

object ReactiveElastic {


  implicit class ReactiveElastic(client: ElasticClient) {

    import ElasticDsl._

    def subscriber[T](config: SubscriberConfig)
                     (implicit builder: RequestBuilder[T], actorRefFactory: ActorRefFactory): BulkIndexingSubscriber[T] = {
      new BulkIndexingSubscriber[T](client, builder, config)
    }

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
                     (implicit builder: RequestBuilder[T], actorRefFactory: ActorRefFactory): BulkIndexingSubscriber[T] = {
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
      subscriber(config)
    }

    def publisher(indexType: IndexAndTypes, elements: Long = Long.MaxValue, keepAlive: String = "1m")
                 (implicit actorRefFactory: ActorRefFactory): ScrollPublisher = {
      publisher(search in indexType query "*:*" scroll keepAlive)
    }

    def publisher(q: SearchDefinition)(implicit actorRefFactory: ActorRefFactory): ScrollPublisher = publisher(q, Long.MaxValue)
    def publisher(q: SearchDefinition, elements: Long)
                 (implicit actorRefFactory: ActorRefFactory): ScrollPublisher = {
      new ScrollPublisher(client, q, elements)
    }
  }


}

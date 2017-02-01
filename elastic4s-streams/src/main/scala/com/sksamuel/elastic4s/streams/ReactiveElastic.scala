package com.sksamuel.elastic4s.streams

import akka.actor.ActorRefFactory
import com.sksamuel.elastic4s.searches.SearchDefinition
import com.sksamuel.elastic4s.{TcpClient$, IndexesAndTypes}

import scala.concurrent.duration._
import scala.language.implicitConversions

object ReactiveElastic {

  implicit class ReactiveElastic(client: TcpClient) {

    import com.sksamuel.elastic4s.ElasticDsl._

    def subscriber[T](config: SubscriberConfig)
                     (implicit builder: RequestBuilder[T], actorRefFactory: ActorRefFactory): BulkIndexingSubscriber[T] = {
      new BulkIndexingSubscriber[T](client, builder, TypedSubscriberConfig(config))
    }

    def subscriber[T](extendedConfig: TypedSubscriberConfig[T])
                     (implicit builder: RequestBuilder[T], actorRefFactory: ActorRefFactory): BulkIndexingSubscriber[T] = {
      new BulkIndexingSubscriber[T](client, builder, extendedConfig)
    }

    def subscriber[T](batchSize: Int = 100,
                      concurrentRequests: Int = 5,
                      refreshAfterOp: Boolean = false,
                      listener: ResponseListener = ResponseListener.noop,
                      typedListener: TypedResponseListener[T] = TypedResponseListener.noop,
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
      ).withTypedListener(typedListener)
      subscriber(config)
    }

    def publisher(indexesTypes: IndexesAndTypes,
                  elements: Long = Long.MaxValue,
                  keepAlive: String = "1m")
                 (implicit actorRefFactory: ActorRefFactory): ScrollPublisher = {
      publisher(search(indexesTypes) query "*:*" scroll keepAlive)
    }

    def publisher(q: SearchDefinition)(implicit actorRefFactory: ActorRefFactory): ScrollPublisher = publisher(q, Long.MaxValue)
    def publisher(q: SearchDefinition, elements: Long)
                 (implicit actorRefFactory: ActorRefFactory): ScrollPublisher = {
      new ScrollPublisher(client, q, elements)
    }
  }
}

package com.sksamuel.elastic4s.akka.reactivestreams

import akka.actor.ActorRefFactory

import com.sksamuel.elastic4s.requests.searches.SearchRequest
import com.sksamuel.elastic4s.{ElasticClient, Indexes}
import scala.concurrent.Future
import scala.concurrent.duration._

object ReactiveElastic {
  sealed trait Impl
  case object Scroll      extends Impl
  case object SearchAfter extends Impl

  implicit class ReactiveElastic(client: ElasticClient[Future]) {

    import com.sksamuel.elastic4s.ElasticDsl._

    def subscriber[T](config: SubscriberConfig[T])(implicit
        builder: RequestBuilder[T],
        actorRefFactory: ActorRefFactory
    ): BulkIndexingSubscriber[T] =
      new BulkIndexingSubscriber[T](client, builder, config)

    def subscriber[T](
        batchSize: Int = 100,
        concurrentRequests: Int = 5,
        refreshAfterOp: Boolean = false,
        listener: ResponseListener[T] = ResponseListener.noop,
        typedListener: ResponseListener[T] = ResponseListener.noop,
        completionFn: () => Unit = () => (),
        errorFn: Throwable => Unit = _ => (),
        flushInterval: Option[FiniteDuration] = None,
        flushAfter: Option[FiniteDuration] = None,
        failureWait: FiniteDuration = 2.seconds,
        maxAttempts: Int = 5
    )(implicit builder: RequestBuilder[T], actorRefFactory: ActorRefFactory): BulkIndexingSubscriber[T] = {
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

    def publisher(indexes: Indexes, elements: Long, keepAlive: String, mode: Impl)(
        implicit actorRefFactory: ActorRefFactory
    ): PaginatedPublisher =
      publisher(search(indexes).query("*:*").scroll(keepAlive), elements, mode)

    def publisher(q: SearchRequest)(implicit actorRefFactory: ActorRefFactory): PaginatedPublisher =
      publisher(q, Long.MaxValue, Scroll)

    def publisher(q: SearchRequest, mode: Impl)(implicit actorRefFactory: ActorRefFactory): PaginatedPublisher =
      publisher(q, Long.MaxValue, mode)

    def publisher(q: SearchRequest, elements: Long, mode: Impl = Scroll)(implicit
        actorRefFactory: ActorRefFactory
    ): PaginatedPublisher = {
      new PaginatedPublisher(client, q, elements, mode)
    }
  }
}

package com.sksamuel.elastic4s.http

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * An converts an asynchronous computation into a synchronous computation, used by the SearchIterator.
  * An instance of this must be available for `F` * in order to use the SearchIterator, however
  * implementation is trivial for basically all `F's`
 */
trait Awaitable[F[_]] {
  def await[A](effect: F[A], duration: Duration = Duration.Inf): A
}

object Awaitable {
  def apply[F[_]: Awaitable]: Awaitable[F] = implicitly[Awaitable[F]]

  implicit object AwaitableFuture extends Awaitable[Future] {
    override def await[A](effect: Future[A], duration: Duration = Duration.Inf): A = Await.result(effect, duration)
  }
}

package com.sksamuel.elastic4s.http

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/*
  An awaitable typeclass so that the SearchScroll API can Await scroll requests. Different effect types. Unsure
  yet if the various `execute` methods will need to be constrained over this typeclass to make scroll work.
  TODO: Verify this.
 */
trait Awaitable[F[_]] {
  def await[A](effect: F[A], duration: Duration = Duration.Inf): A
}

object Awaitable {
  implicit object AwaitableFuture extends Awaitable[Future] {
    override def await[A](effect: Future[A], duration: Duration = Duration.Inf): A = Await.result(effect, duration)
  }
}

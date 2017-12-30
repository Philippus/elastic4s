package com.sksamuel.elastic4s.http

/*
  An awaitable typeclass so that the SearchScroll API can Await scroll requests. Different effect types. Unsure
  yet if the various `execute` methods will need to be constrained over this typeclass to make scroll work.
  TODO: Verify this.
 */
trait Awaitable[F[_]] {
  def await[A](effect: F[A]): A
}

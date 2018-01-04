package com.sksamuel.elastic4s.http

import cats.Functor

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  *  A typeclass that wraps an asynchronous computation.
  *
  *  Ideally, we'd paramterize on Functor instead of extending, but extending allows us to introduce this
  *  without breaking API changes. TODO: In the next major release, F[_] should be parameterized on Functor.
  */
trait AsyncExecutor[F[_]] extends Functor[F] {
  /**
    * Creates an `F[HttpResponse]` instance from a provided function
    * that will have a callback injected for signaling the
    * final result of an asynchronous process.
    *
    * @param k is a function that should be called with a
    *       callback for signaling the result once it is ready
    *
    * Shamelessly stolen from https://github.com/typelevel/cats-effect/blob/master/core/shared/src/main/scala/cats/effect/Async.scala
    */
  def async(k: (Either[Exception, HttpResponse] => Unit) => Unit): F[HttpResponse]
}

object AsyncExecutor {
  def apply[F[_]: AsyncExecutor]: AsyncExecutor[F] = implicitly[AsyncExecutor[F]]
  implicit def scalaFutureAsyncExecutor(
      implicit ec: ExecutionContext = ExecutionContext.Implicits.global): AsyncExecutor[Future] =
    new AsyncExecutor[Future] {
      override def async(
          k: (Either[Exception, HttpResponse] => Unit) => Unit): Future[HttpResponse] = {

        val p = Promise[HttpResponse]()

        k {
          case Left(exc) => p.tryFailure(exc)
          case Right(r)  => p.trySuccess(r)
        }

        p.future
      }

      override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)
    }
}

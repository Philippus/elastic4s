package com.sksamuel.elastic4s

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {

  def apply[F[_]: Functor](): Functor[F] = implicitly[Functor[F]]

  implicit def FutureFunctor(implicit ec: ExecutionContext = ExecutionContext.Implicits.global): Functor[Future] =
    new Functor[Future] {
      override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)
    }
}

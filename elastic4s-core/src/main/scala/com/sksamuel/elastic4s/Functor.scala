package com.sksamuel.elastic4s

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {

  def apply[F[_]](implicit f: Functor[F]): Functor[F] = f

  implicit def FutureFunctor(implicit ec: ExecutionContext = ExecutionContext.global): Functor[Future] =
    new Functor[Future] {
      override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)
    }
}

object FunctorSyntax {

  implicit class FunctorOps[F[_], A](fa: F[A]) {
    def map[B](f: A => B)(implicit fun: Functor[F]): F[B] = fun.map(fa)(f)
  }

}

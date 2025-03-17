package com.sksamuel.elastic4s.cats.effect.instances

import cats.{Functor => CatsFunctor}
import com.sksamuel.elastic4s.Functor

import scala.language.higherKinds

trait CatsEffectInstances {
  implicit def catsFunctor[F[_]: CatsFunctor]: Functor[F] = new Functor[F] {
    override def map[A, B](fa: F[A])(f: A => B): F[B] = CatsFunctor[F].map(fa)(f)
  }
}

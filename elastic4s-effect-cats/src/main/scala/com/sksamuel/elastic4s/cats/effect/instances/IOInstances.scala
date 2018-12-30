package com.sksamuel.elastic4s.cats.effect.instances

import cats.effect.IO
import com.sksamuel.elastic4s.Functor
import com.sksamuel.elastic4s.cats.effect.IOExecutor

trait IOInstances {
  implicit val ioFunctor: Functor[IO] = new Functor[IO] {
    override def map[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)
  }

  implicit val ioExecutor: IOExecutor = new IOExecutor
}

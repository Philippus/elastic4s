package com.sksamuel.elastic4s.zio.instances

import com.sksamuel.elastic4s.Functor
import zio.Task

trait TaskInstances {
  implicit val taskFunctor: Functor[Task] = new Functor[Task] {
    override def map[A, B](fa: Task[A])(f: A => B): Task[B] = fa.map(f)
  }
}

package com.sksamuel.elastic4s.monix.instances

import com.sksamuel.elastic4s.Functor
import monix.eval.Task

trait TaskInstances {
  implicit val taskFunctor: Functor[Task] = new Functor[Task] {
    override def map[A, B](fa: Task[A])(f: A => B): Task[B] = fa.map(f)
  }
}

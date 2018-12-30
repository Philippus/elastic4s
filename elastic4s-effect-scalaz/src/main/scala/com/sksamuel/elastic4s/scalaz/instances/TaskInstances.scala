package com.sksamuel.elastic4s.scalaz.instances

import com.sksamuel.elastic4s.Functor
import com.sksamuel.elastic4s.scalaz.TaskExecutor
import scalaz.concurrent.Task

trait TaskInstances {
  implicit val taskFunctor: Functor[Task] = new Functor[Task] {
    override def map[A, B](fa: Task[A])(f: A => B): Task[B] = fa.map(f)
  }

  implicit val taskExecutor: TaskExecutor = new TaskExecutor
}

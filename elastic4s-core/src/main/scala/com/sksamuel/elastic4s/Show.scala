package com.sksamuel.elastic4s

trait Show[T] extends Serializable {
  def show(t: T): String
}

object Show {
  def apply[T: Show]: Show[T] = implicitly[Show[T]]
}

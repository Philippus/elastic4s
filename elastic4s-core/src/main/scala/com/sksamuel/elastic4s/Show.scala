package com.sksamuel.elastic4s

trait Show[T] extends Serializable {
  def show(t: T): String
}

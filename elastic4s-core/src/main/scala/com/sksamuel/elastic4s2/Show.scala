package com.sksamuel.elastic4s2

/**
 * A typeclass to provide a json representation of a request.
 * Not all requests can be shown.
 */
trait Show[T] extends Serializable {
  def show(f: T): String
}


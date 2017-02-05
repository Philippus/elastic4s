package com.sksamuel.elastic4s

/**
  * Typeclass to be implemented by the various json modules for use in the http client.
  *
  * @tparam U the type of the class supported
  */
trait JsonFormat[+U] {
  def fromJson(string: String): U
}

package com.sksamuel.elastic4s

/** A typeclass that is used by index/update requests to convert a scala type into a document that elasticsearch can
  * use.
  *
  * Indexables can be automatically derived if you add a deriving module like `elastic4s-json-jackson` or
  * `elastic4s-json-circe`.
  */
trait Indexable[T] {
  def json(t: T): String
}

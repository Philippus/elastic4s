package com.sksamuel.elastic4s

/**
  * A Typeclass that is used by index requests to convert a type into a document for use by Elasticsearch.
  *
  * Indexables can be automatically derived if you add a module like `elastic4s-json-jackson` or `elastic4s-json-circe`.
  */
trait Indexable[T] {
  def json(t: T): String
}

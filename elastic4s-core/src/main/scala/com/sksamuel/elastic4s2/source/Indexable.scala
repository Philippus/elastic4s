package com.sksamuel.elastic4s2.source

/** A Typeclass that is used by index requests to convert a type into a document for use by Elasticsearch
  */
trait Indexable[T] {
  def json(t: T): String
}

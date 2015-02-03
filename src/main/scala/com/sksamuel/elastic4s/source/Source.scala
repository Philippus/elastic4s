package com.sksamuel.elastic4s.source

/** @author Stephen Samuel */
trait DocumentSource {
  def json: String
}

case class StringDocumentSource(str: String) extends DocumentSource {
  override def json = str
}

trait DocumentMap {
  def map: Map[String, Any]
}

trait Indexable[T] {
  def json(t: T): String
}

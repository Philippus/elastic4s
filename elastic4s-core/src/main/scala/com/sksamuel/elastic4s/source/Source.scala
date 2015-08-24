package com.sksamuel.elastic4s.source

/** @author Stephen Samuel */
trait DocumentSource {
  def json: String
}

/** An instance of DocumentSource that just provides json as is
  */
case class JsonDocumentSource(j: String) extends DocumentSource {
  override def json = j
}

trait DocumentMap {
  def map: Map[String, Any]
}


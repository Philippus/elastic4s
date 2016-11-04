package com.sksamuel.elastic4s

import org.elasticsearch.common.bytes.BytesReference

/**
  * A common trait for Get and Search API results so that Readable typeclass
  * can unmarshall either get or search results into a type.
  */
trait Hit {

  def id: String
  def index: String
  def `type`: String
  def version: Long

  def ref: DocumentRef = DocumentRef(index, `type`, id)

  def sourceField(name: String): AnyRef = sourceAsMap(name)
  def sourceFieldOpt(name: String): Option[AnyRef] = sourceAsMap.get(name)

  def sourceAsMap: Map[String, AnyRef]
  def sourceAsBytes: Array[Byte]
  def sourceAsString: String
  def sourceAsByteRef: BytesReference
  def isSourceEmpty: Boolean

  def exists: Boolean
}

trait HitField {
  def name: String
  def value: AnyRef
  def values: Seq[AnyRef]
  def isMetadataField: Boolean
}

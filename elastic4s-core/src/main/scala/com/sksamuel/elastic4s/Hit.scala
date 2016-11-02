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

  def sourceAsMap: Map[String, AnyRef]
  def sourceAsBytes: Array[Byte]
  def sourceAsString: String
  def sourceAsByteRef: BytesReference
  def isSourceEmpty: Boolean

  def exists: Boolean

  def field(name: String): HitField
  def fieldOpt(name: String): Option[HitField]
  def fields: Map[String, HitField]
}

trait HitField {
  def name: String
  def value: AnyRef
  def values: Seq[AnyRef]
  def isMetadataField: Boolean
}

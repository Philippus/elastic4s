package com.sksamuel.elastic4s

import org.scalactic.{ErrorMessage, Or}

trait Hit {
  def id: String
  def index: String

  def source: Map[String, AnyRef]
  def sourceAsBytes: Array[Byte]
  def sourceAsString: String

  def isExists: Boolean
  def isEmpty: Boolean

  def `type`: String
  def version: Long

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

trait HitReader[T] {
  def from(hit: Hit): T Or ErrorMessage
}

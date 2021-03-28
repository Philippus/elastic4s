package com.sksamuel.elastic4s.requests.get

trait HitField {
  def name: String
  def value: AnyRef
  def values: Seq[AnyRef]
  def isMetadataField: Boolean
}

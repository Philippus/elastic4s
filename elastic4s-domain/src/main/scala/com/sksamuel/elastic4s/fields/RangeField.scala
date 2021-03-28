package com.sksamuel.elastic4s.fields

trait RangeField extends ElasticField {
  def boost: Option[Double]
  def coerce: Option[Boolean]
  def index: Option[Boolean]
  def store: Option[Boolean]
}

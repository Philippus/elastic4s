package com.sksamuel.elastic4s.fields

trait NumberField[T] extends ElasticField {
  def boost: Option[Double]
  def coerce: Option[Boolean]
  def ignoreMalformed: Option[Boolean]
  def index: Option[Boolean]
  def store: Option[Boolean]
  def docValues: Option[Boolean]
  def nullValue: Option[T]
  def copyTo: Seq[String]
}

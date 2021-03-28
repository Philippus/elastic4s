package com.sksamuel.elastic4s.fields

trait ElasticField {
  def name: String
  def `type`: String // defines the elasticsearch constant used for this type, eg "integer" or "geo_shape"
}

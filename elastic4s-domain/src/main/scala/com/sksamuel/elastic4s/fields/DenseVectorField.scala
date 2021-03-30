package com.sksamuel.elastic4s.fields

case class DenseVectorField(name: String,
                            dims: Int) extends ElasticField {
  override def `type`: String = "dense_vector"
}

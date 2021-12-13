package com.sksamuel.elastic4s.fields

object DenseVectorField {
  val `type`: String = "dense_vector"
}
case class DenseVectorField(name: String,
                            dims: Int) extends ElasticField {
  override def `type`: String = DenseVectorField.`type`
}

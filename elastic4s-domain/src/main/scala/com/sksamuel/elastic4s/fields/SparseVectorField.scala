package com.sksamuel.elastic4s.fields

object SparseVectorField {
  val `type`: String = "sparse_vector"
}

case class SparseVectorField(name: String) extends ElasticField {
  override def `type`: String = SparseVectorField.`type`
}

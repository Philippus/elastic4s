package com.sksamuel.elastic4s.fields

object DenseVectorField {
  val `type`: String = "dense_vector"
}
sealed trait Similarity {
  def name: String
}
case object L2Norm extends Similarity { val name = "l2_norm" }
case object DotProduct extends Similarity { val name = "dot_product" }
case object Cosine extends Similarity { val name = "cosine" }

case class DenseVectorField(name: String,
                            dims: Int,
                            index: Boolean = false,
                            similarity: Similarity = L2Norm) extends ElasticField {
  override def `type`: String = DenseVectorField.`type`
}

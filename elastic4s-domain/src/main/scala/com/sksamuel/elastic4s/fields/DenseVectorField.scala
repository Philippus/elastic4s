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
                            similarity: Similarity = L2Norm,
                            indexOptions: Option[DenseVectorIndexOptions] = None) extends ElasticField {
  override def `type`: String = DenseVectorField.`type`
}

sealed trait KnnAlgorithmType {
  def name: String
}
case object Hnsw extends KnnAlgorithmType { val name = "hnsw" }
case object Int8Hnsw extends KnnAlgorithmType { val name = "int8_hnsw" }
case object Flat extends KnnAlgorithmType { val name = "flat" }
case object Int8Flat extends KnnAlgorithmType { val name = "int8_flat" }

case class DenseVectorIndexOptions(`type`: KnnAlgorithmType,
                                   m: Option[Int] = None,
                                   efConstruction: Option[Int] = None,
                                   confidenceInterval: Option[Double] = None)

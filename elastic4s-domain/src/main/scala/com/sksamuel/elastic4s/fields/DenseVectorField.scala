package com.sksamuel.elastic4s.fields

object DenseVectorField {
  val `type`: String = "dense_vector"

  sealed trait KnnType {
    def name: String
  }
  case object Hnsw extends KnnType { val name = "hnsw" }
  case object Int8Hnsw extends KnnType { val name = "int8_hnsw" }
  case object Flat extends KnnType { val name = "flat" }
  case object Int8Flat extends KnnType { val name = "int8_flat" }
}

sealed trait Similarity {
  def name: String
}
case object L2Norm extends Similarity { val name = "l2_norm" }
case object DotProduct extends Similarity { val name = "dot_product" }
case object Cosine extends Similarity { val name = "cosine" }
case object MaxInnerProduct extends Similarity { val name = "max_inner_product" }

case class DenseVectorField(name: String,
                            dims: Int,
                            index: Boolean = false,
                            similarity: Similarity = L2Norm,
                            indexOptions: Option[DenseVectorIndexOptions] = None,
                            elementType: Option[String] = None) extends ElasticField {
  override def `type`: String  = DenseVectorField.`type`

  def dims(dims: Int): DenseVectorField = copy(dims = dims)

  def index(index: Boolean): DenseVectorField = copy(index = index)

  def similarity(similarity: Similarity): DenseVectorField = copy(similarity = similarity)

  def elementType(elementType: String): DenseVectorField = copy(elementType = Some(elementType))
}

case class DenseVectorIndexOptions(`type`: DenseVectorField.KnnType, m: Option[Int] = None, efConstruction: Option[Int] = None, confidenceInterval: Option[Double] = None)

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

  @deprecated("Use the new apply method", "8.14.0")
  def apply(name: String,
            dims: Int): DenseVectorField =
    DenseVectorField(name, None, Some(dims), Some(false), Some(L2Norm))

  @deprecated("Use the new apply method", "8.14.0")
  def apply(name: String,
            dims: Int,
            index: Boolean): DenseVectorField =
    DenseVectorField(name, None, Some(dims), Some(index), Some(L2Norm))

  @deprecated("Use the new apply method", "8.14.0")
  def apply(name: String,
            dims: Int,
            index: Boolean,
            similarity: Similarity): DenseVectorField =
    DenseVectorField(name, None, Some(dims), Some(index), Some(similarity))
}

sealed trait Similarity {
  def name: String
}

case object L2Norm extends Similarity { val name = "l2_norm" }
case object DotProduct extends Similarity { val name = "dot_product" }
case object Cosine extends Similarity { val name = "cosine" }
case object MaxInnerProduct extends Similarity { val name = "max_inner_product" }

case class DenseVectorIndexOptions(`type`: DenseVectorField.KnnType, m: Option[Int] = None, efConstruction: Option[Int] = None, confidenceInterval: Option[Float] = None) {

}

case class DenseVectorField(name: String,
                            elementType: Option[String] = None,
                            dims: Option[Int] = None,
                            index: Option[Boolean] = None,
                            similarity: Option[Similarity] = None,
                            indexOptions: Option[DenseVectorIndexOptions] = None) extends ElasticField {
  override def `type`: String = DenseVectorField.`type`

  def elementType(elementType: String): DenseVectorField = copy(elementType = Some(elementType))
  def dims(dims: Int): DenseVectorField = copy(dims = Some(dims))
  def index(index: Boolean): DenseVectorField = copy(index = Some(index))
  def similarity(similarity: Similarity): DenseVectorField = copy(similarity = Some(similarity))
  def indexOptions(indexOptions: DenseVectorIndexOptions): DenseVectorField = copy(indexOptions = Some(indexOptions))
}

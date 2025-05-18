package com.sksamuel.elastic4s.fields

object DenseVectorField {
  val `type`: String = "dense_vector"

  sealed trait KnnType {
    def name: String
  }
  case object Hnsw     extends KnnType { val name = "hnsw"      }
  case object Int8Hnsw extends KnnType { val name = "int8_hnsw" }
  case object Int4Hnsw extends KnnType { val name = "int4_hnsw" }
  case object BbqHnsw  extends KnnType { val name = "bbq_hnsw"  }
  case object Flat     extends KnnType { val name = "flat"      }
  case object Int8Flat extends KnnType { val name = "int8_flat" }
  case object Int4Flat extends KnnType { val name = "int4_flat" }
  case object BbqFlat  extends KnnType { val name = "bbq_flat"  }
}

sealed trait Similarity {
  def name: String
}

case object L2Norm          extends Similarity { val name = "l2_norm"           }
case object DotProduct      extends Similarity { val name = "dot_product"       }
case object Cosine          extends Similarity { val name = "cosine"            }
case object MaxInnerProduct extends Similarity { val name = "max_inner_product" }

case class DenseVectorIndexOptions(
    `type`: DenseVectorField.KnnType,
    m: Option[Int] = None,
    efConstruction: Option[Int] = None,
    confidenceInterval: Option[Float] = None
) {}

case class DenseVectorField(
    name: String,
    elementType: Option[String] = None,
    dims: Option[Int] = None,
    index: Option[Boolean] = None,
    similarity: Option[Similarity] = None,
    indexOptions: Option[DenseVectorIndexOptions] = None
) extends ElasticField {
  override def `type`: String = DenseVectorField.`type`

  def elementType(elementType: String): DenseVectorField                    = copy(elementType = Some(elementType))
  def dims(dims: Int): DenseVectorField                                     = copy(dims = Some(dims))
  def index(index: Boolean): DenseVectorField                               = copy(index = Some(index))
  def similarity(similarity: Similarity): DenseVectorField                  = copy(similarity = Some(similarity))
  def indexOptions(indexOptions: DenseVectorIndexOptions): DenseVectorField = copy(indexOptions = Some(indexOptions))
}

package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.requests.searches.queries.PruningConfig

object SparseVectorField {
  val `type`: String = "sparse_vector"
}

case class SparseVectorIndexOptions(prune: Boolean, pruningConfig: Option[PruningConfig])

case class SparseVectorField(
    name: String,
    store: Boolean = false,
    indexOptions: Option[SparseVectorIndexOptions] = None
) extends ElasticField {
  override def `type`: String = SparseVectorField.`type`
}

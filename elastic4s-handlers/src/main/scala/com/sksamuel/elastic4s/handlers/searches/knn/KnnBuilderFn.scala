package com.sksamuel.elastic4s.handlers.searches.knn

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.knn.Knn

object KnnBuilderFn {
  def apply(knn: Knn): XContentBuilder = {
    val builder: XContentBuilder = XContentFactory.jsonBuilder()
    builder.field("field", knn.field)
    builder.array("query_vector", knn.queryVector.toArray)
    builder.field("k", knn.k)
    builder.field("num_candidates", knn.numCandidates)
    knn.similarity match {
      case Some(value) => builder.field("similarity", value)
      case _ =>
    }
    builder.field("boost", knn.boost)
    builder.endObject()
    builder
  }
}

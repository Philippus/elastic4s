package com.sksamuel.elastic4s.handlers.searches.knn

import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.handlers.searches.queries.nested.InnerHitQueryBodyBuilder
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.knn.Knn

object KnnBuilderFn {
  def apply(knn: Knn): XContentBuilder = {
    val builder: XContentBuilder = XContentFactory.jsonBuilder()
    builder.field("field", knn.field)
    knn.filter.foreach(filter => builder.rawField("filter", QueryBuilderFn(filter)))
    knn.k.foreach(builder.field("k", _))
    knn.numCandidates.foreach(builder.field("num_candidates", _))
    builder.array("query_vector", knn.queryVector.toArray)
    knn.similarity.foreach(builder.field("similarity", _))
    knn.boost.foreach(builder.field("boost", _))
    knn.inner.foreach(inner => builder.field("inner_hits", InnerHitQueryBodyBuilder.toJson(inner)))
    builder.endObject()
    builder
  }
}

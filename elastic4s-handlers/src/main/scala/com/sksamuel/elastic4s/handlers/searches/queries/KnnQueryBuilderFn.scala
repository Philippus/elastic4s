package com.sksamuel.elastic4s.handlers.searches.queries

import com.sksamuel.elastic4s.handlers.searches.knn.KnnBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.knn.Knn

object KnnQueryBuilderFn {
  def apply(knn: Knn): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("knn", KnnBuilderFn.apply(knn))
    builder
  }
}

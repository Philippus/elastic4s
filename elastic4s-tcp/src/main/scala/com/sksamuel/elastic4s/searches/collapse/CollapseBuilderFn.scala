package com.sksamuel.elastic4s.searches.collapse

import com.sksamuel.elastic4s.searches.queries.InnerHitBuilder
import org.elasticsearch.search.collapse.CollapseBuilder

object CollapseBuilderFn {
  def apply(collapse: CollapseDefinition): CollapseBuilder = {
    val builder = new CollapseBuilder(collapse.field)
    collapse.inner.foreach(inner => builder.setInnerHits(InnerHitBuilder.apply(inner)))
    collapse.maxConcurrentGroupSearches.foreach(builder.setMaxConcurrentGroupRequests)
    builder
  }
}

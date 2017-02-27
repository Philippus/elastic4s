package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.searches.aggs.{AggregationDefinition, TermsAggregationDefinition}
import org.elasticsearch.common.xcontent.XContentBuilder

object AggregationBuilderFn {
  def apply(agg: AggregationDefinition): XContentBuilder = agg match {
    case agg: TermsAggregationDefinition => TermsAggregationBuilder(agg)
  }
}

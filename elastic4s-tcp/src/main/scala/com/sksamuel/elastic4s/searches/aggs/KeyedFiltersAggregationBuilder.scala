package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregationBuilder
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregator.KeyedFilter

object KeyedFiltersAggregationBuilder {

  import scala.collection.JavaConverters._

  def apply(agg: KeyedFiltersAggregationDefinition): FiltersAggregationBuilder = {

    val builder = AggregationBuilders.filters(
      agg.name,
      agg.filters.map {
        case (name, query) => new KeyedFilter(name, QueryBuilderFn(query))
      }.toSeq: _*
    )

    agg.otherBucket.foreach(builder.otherBucket)
    agg.otherBucketKey.foreach(builder.otherBucketKey)

    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}

package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.{Terms, TermsAggregationBuilder}

import scala.collection.JavaConverters._

object TermsAggregationBuilder {

  def apply(agg: TermsAggregationDefinition): TermsAggregationBuilder = {

    val builder = AggregationBuilders.terms(agg.name)

    agg.field.foreach(builder.field)
    agg.collectMode.foreach(builder.collectMode)
    agg.executionHint.foreach(builder.executionHint)
    agg.includeExclude.foreach(builder.includeExclude)
    agg.minDocCount.foreach(builder.minDocCount)
    agg.missing.foreach(builder.missing)
    agg._oldOrder.foreach(builder.order)
    agg.order.foreach {
      case TermsOrder("_count", asc) => builder.order(Terms.Order.count(asc))
      case TermsOrder("_term", asc) => builder.order(Terms.Order.term(asc))
      case TermsOrder(field, asc) if field.contains(".") =>
        val parts = field.split('.')
        builder.order(Terms.Order.aggregation(parts(0), parts(1), asc))
      case TermsOrder(field, asc) => builder.order(Terms.Order.aggregation(field, asc))
    }
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    agg.shardSize.foreach(builder.shardSize)
    agg.showTermDocCountError.foreach(builder.showTermDocCountError)
    agg.size.foreach(builder.size)
    agg.valueType.foreach(builder.valueType)

    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)

    builder
  }
}

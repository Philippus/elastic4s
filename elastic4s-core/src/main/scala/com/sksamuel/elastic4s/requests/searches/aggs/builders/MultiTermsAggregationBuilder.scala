package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.{EnumConversions, handlers}
import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AbstractAggregation, SubAggsBuilderFn, MultiTermsAggregation}

object MultiTermsAggregationBuilder {
  def apply(agg: MultiTermsAggregation, customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("multi_terms")

    if (agg.terms.nonEmpty) {
      builder.startArray("terms")
      agg.terms.foreach { term =>
        val termBuilder = builder.startObject()
        term.field.foreach(termBuilder.field("field", _))
        term.missing.foreach(termBuilder.autofield("missing", _))
        term.collectMode.map(EnumConversions.collectMode).foreach(termBuilder.field("collect_mode", _))
        term.shardMinDocCount.foreach(termBuilder.field("shard_min_doc_count", _))
        term.shardSize.foreach(termBuilder.field("shard_size", _))
        term.showTermDocCountError.foreach(termBuilder.field("show_term_doc_count_error", _))
        termBuilder.endObject()
      }
      builder.endArray()
    }
    agg.size.foreach(builder.field("size", _))
    agg.minDocCount.foreach(builder.field("min_doc_count", _))

    agg.script.foreach { script =>
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
    }

    agg.orders match {
      case order if order.isEmpty =>
      case Seq(order)             => builder.rawField("order", EnumConversions.order(order))
      case _ =>
        builder.startArray("order")
        agg.orders.map(EnumConversions.order).foreach(builder.rawValue)
        builder.endArray()
    }

    builder.endObject()

    SubAggsBuilderFn(agg, builder, customAggregations)
    builder.endObject()
  }
}

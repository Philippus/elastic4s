package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.searches.aggs.TermsAggregationDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object TermsAggregationBuilder {
  def apply(agg: TermsAggregationDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject().startObject("terms")

    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.field("missing", _))
    agg.executionHint.foreach(builder.field("execution_hint", _))
    agg.collectMode.map(_.parseField.getPreferredName).foreach(builder.field("collect_mode", _))
    agg.size.foreach(builder.field("size", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script).bytes, XContentType.JSON)
    }
    agg.includeExclude.foreach { inex =>
      inex.toXContent(builder, null)
    }
    agg.minDocCount.foreach(builder.field("min_doc_count", _))
    agg.shardMinDocCount.foreach(builder.field("shard_min_doc_count", _))
    agg.shardSize.foreach(builder.field("shard_size", _))
    agg.showTermDocCountError.foreach(builder.field("show_term_doc_count_error", _))
    agg.order.foreach(builder.field("order", _))

    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    builder.endObject()
  }
}



package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.CommonTermsQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object CommonTermsQueryBodyFn {
  def apply(q: CommonTermsQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("common")
    builder.startObject(q.name)
    builder.field("query", q.text)

    q.lowFreqMinimumShouldMatch.map(_.toString).foreach(builder.field("low_freq", _))
    q.highFreqMinimumShouldMatch.map(_.toString).foreach(builder.field("high_freq", _))
    q.lowFreqOperator.map(_.toString).foreach(builder.field("low_freq_operator", _))
    q.highFreqOperator.map(_.toString).foreach(builder.field("high_freq_operator", _))
    q.minimumShouldMatch.map(_.toString).foreach(builder.field("minimum_should_match", _))

    q.analyzer.map(_.toString).foreach(builder.field("analyzer", _))
    q.cutoffFrequency.map(_.toString).foreach(builder.field("cutoff_frequency", _))
    q.boost.foreach(builder.field("boost", _))
    q.disableCoord.foreach(builder.field("disable_coord", _))

    builder.endObject()
    builder.endObject()
    builder.endObject()
  }
}

package com.sksamuel.elastic4s.http.search.queries.text

import com.sksamuel.elastic4s.searches.queries.CommonTermsQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object CommonTermsQueryBodyFn {
  def apply(q: CommonTermsQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("common")
    builder.startObject(q.name)
    builder.field("query", q.text)

    q.lowFreqOperator.map(_.toString).foreach(builder.field("low_freq_operator", _))
    q.highFreqOperator.map(_.toString).foreach(builder.field("high_freq_operator", _))

    q.minimumShouldMatch match {
      case Some(minimumShouldMatchString) => builder.field("minimum_should_match", minimumShouldMatchString)
      case _ =>
        builder.startObject("minimum_should_match")
        q.lowFreqMinimumShouldMatch.foreach(builder.field("low_freq", _))
        q.highFreqMinimumShouldMatch.foreach(builder.field("high_freq", _))
        builder.endObject()
    }

    q.analyzer.map(_.toString).foreach(builder.field("analyzer", _))
    q.cutoffFrequency.map(_.toString).foreach(builder.field("cutoff_frequency", _))
    q.boost.foreach(builder.field("boost", _))
    q.disableCoord.foreach(builder.field("disable_coord", _))

    builder.endObject()
    builder.endObject()
    builder.endObject()
  }
}

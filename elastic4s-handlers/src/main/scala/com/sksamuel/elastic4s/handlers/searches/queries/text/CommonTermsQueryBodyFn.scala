package com.sksamuel.elastic4s.handlers.searches.queries.text

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.CommonTermsQuery

object CommonTermsQueryBodyFn {
  def apply(q: CommonTermsQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("common")
    builder.startObject(q.name)
    builder.field("query", q.text)

    q.lowFreqOperator.foreach(builder.field("low_freq_operator", _))
    q.highFreqOperator.foreach(builder.field("high_freq_operator", _))

    q.minimumShouldMatch match {
      case Some(minimumShouldMatchString) => builder.field("minimum_should_match", minimumShouldMatchString)
      case _ =>
        builder.startObject("minimum_should_match")
        q.lowFreqMinimumShouldMatch.foreach(builder.field("low_freq", _))
        q.highFreqMinimumShouldMatch.foreach(builder.field("high_freq", _))
        builder.endObject()
    }

    q.analyzer.foreach(builder.field("analyzer", _))
    q.cutoffFrequency.map(_.toString).foreach(builder.field("cutoff_frequency", _))
    q.boost.foreach(builder.field("boost", _))

    builder.endObject()
    builder.endObject()
    builder.endObject()
  }
}

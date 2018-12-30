package com.sksamuel.elastic4s.requests.searches.queries.text

import com.sksamuel.elastic4s.requests.searches.queries.matches.{FieldWithOptionalBoost, MultiMatchQuery}
import com.sksamuel.elastic4s.{EnumConversions, XContentBuilder, XContentFactory}

object MultiMatchBodyFn {
  def apply(q: MultiMatchQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("multi_match")
    builder.field("query", q.text)
    builder.array("fields", q.fields.map {
      case FieldWithOptionalBoost(field, Some(boost)) => s"$field^$boost"
      case FieldWithOptionalBoost(field, _)           => field
    }.toArray)
    q.`type`.map(EnumConversions.multiMatchQueryBuilderType).foreach(builder.field("type", _))
    q.analyzer.map(_.toString).foreach(builder.field("analyzer", _))
    q.cutoffFrequency.map(_.toString).foreach(builder.field("cutoff_frequency", _))
    q.fuzziness.map(_.toString).foreach(builder.field("fuzziness", _))
    q.fuzzyRewrite.foreach(builder.field("fuzzy_rewrite", _))
    q.lenient.map(_.toString).foreach(builder.field("lenient", _))
    q.maxExpansions.foreach(builder.field("max_expansions", _))
    q.minimumShouldMatch.map(_.toString).foreach(builder.field("minimum_should_match", _))
    q.operator.map(_.toString).foreach(builder.field("operator", _))
    q.prefixLength.map(_.toString).foreach(builder.field("prefix_length", _))
    q.slop.foreach(builder.field("slop", _))
    q.tieBreaker.foreach(builder.field("tie_breaker", _))
    q.zeroTermsQuery.map(EnumConversions.zeroTermsQuery).foreach(builder.field("zero_terms_query", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
  }
}

package com.sksamuel.elastic4s.http.search.queries.text

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.matches.MultiMatchQueryDefinition

object MultiMatchBodyFn {
  def apply(q: MultiMatchQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("multi_match")
    builder.field("query", q.text)
    builder.field("fields", q.fields.map {
      case (field, 0) => field
      case (field, boost) => s"$field^$boost"
    }.toArray)
    q.`type`.map(_.parseField.getPreferredName).foreach(builder.field("type", _))
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
    q.zeroTermsQuery.map(_.name).foreach(builder.field("zero_terms_query", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
  }
}

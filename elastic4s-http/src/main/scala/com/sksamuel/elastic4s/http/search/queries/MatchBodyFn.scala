package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.matches.MatchQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object MatchBodyFn {
  def apply(q: MatchQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("match")
    builder.startObject(q.field)
    builder.field("query", q.value)
    q.zeroTerms.map(_.toString).foreach(builder.field("zero_terms_query", _))
    q.analyzer.map(_.toString).foreach(builder.field("analyzer", _))
    q.cutoffFrequency.map(_.toString).foreach(builder.field("cutoff_frequency", _))
    q.lenient.map(_.toString).foreach(builder.field("lenient", _))
    q.minimumShouldMatch.map(_.toString).foreach(builder.field("minimum_should_match", _))
    q.operator.map(_.toString).foreach(builder.field("operator", _))
    q.fuzziness.map(_.toString).foreach(builder.field("fuzziness", _))
    q.prefixLength.map(_.toString).foreach(builder.field("prefix_length", _))
    q.fuzzyTranspositions.foreach(builder.field("fuzzy_transpositions", _))
    q.fuzzyRewrite.foreach(builder.field("fuzzy_rewrite", _))
    q.maxExpansions.foreach(builder.field("max_expansions", _))
    q.boost.foreach(builder.field("boost", _))
    builder.endObject()
    builder.endObject()
    builder.endObject()
  }
}

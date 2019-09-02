package com.sksamuel.elastic4s.requests.searches.queries.text

import com.sksamuel.elastic4s.requests.searches.queries.matches.{MatchBoolPrefix}
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object MatchBoolPrefixBodyFn {
  def apply(q: MatchBoolPrefix): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("match_bool_prefix")
    builder.startObject(q.field)
    builder.autofield("query", q.value)
    q.analyzer.foreach(builder.field("analyzer", _))
    q.minimumShouldMatch.foreach(builder.field("minimum_should_match", _))
    q.operator.map(_.toString).foreach(builder.field("operator", _))
    q.fuzziness.map(_.toString).foreach(builder.field("fuzziness", _))
    q.prefixLength.map(_.toString).foreach(builder.field("prefix_length", _))
    q.fuzzyTranspositions.foreach(builder.field("fuzzy_transpositions", _))
    q.fuzzyRewrite.foreach(builder.field("fuzzy_rewrite", _))
    q.maxExpansions.foreach(builder.field("max_expansions", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }
}

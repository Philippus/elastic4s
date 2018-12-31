package com.sksamuel.elastic4s.requests.searches.queries.text

import com.sksamuel.elastic4s.requests.searches.queries.QueryStringQuery
import com.sksamuel.elastic4s.{EnumConversions, XContentBuilder, XContentFactory}

object QueryStringBodyFn {
  def apply(s: QueryStringQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("query_string")
    s.defaultOperator.foreach(builder.field("default_operator", _))
    s.defaultField.foreach(builder.field("default_field", _))
    s.analyzer.map(_.toString).foreach(builder.field("analyzer", _))
    s.analyzeWildcard.map(_.toString).foreach(builder.field("analyze_wildcard", _))
    s.lenient.map(_.toString).foreach(builder.field("lenient", _))
    s.minimumShouldMatch.map(_.toString).foreach(builder.field("minimum_should_match", _))
    s.fuzzyMaxExpansions.map(_.toString).foreach(builder.field("fuzzy_max_expansions", _))
    s.fuzzyPrefixLength.map(_.toString).foreach(builder.field("fuzzy_prefix_length", _))
    s.fuzziness.map(_.toString).foreach(builder.field("fuzziness", _))
    s.phraseSlop.map(_.toString).foreach(builder.field("phrase_slop", _))
    s.autoGeneratePhraseQueries.map(_.toString).foreach(builder.field("auto_generate_phrase_queries", _))
    s.allowLeadingWildcard.map(_.toString).foreach(builder.field("allow_leading_wildcard", _))
    s.enablePositionIncrements.map(_.toString).foreach(builder.field("enable_position_increments", _))
    s.boost.foreach(builder.field("boost", _))
    s.quoteFieldSuffix.map(_.toString).foreach(builder.field("quote_field_suffix", _))
    s.splitOnWhitespace.map(_.toString).foreach(builder.field("split_on_whitespace", _))
    s.tieBreaker.foreach(builder.field("tie_breaker", _))
    s.`type`.map(EnumConversions.multiMatchQueryBuilderType).foreach(builder.field("type", _))
    s.rewrite.map(_.toString).foreach(builder.field("rewrite", _))

    if (s.fields.nonEmpty) {
      val fields = s.fields.map {
        case (name, boost) => boost.fold(name)(boost => s"$name^$boost")
      }.toArray
      builder.array("fields", fields)
    }

    builder.field("query", s.query)

    builder.endObject()
    builder.endObject()
  }
}

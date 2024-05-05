package com.sksamuel.elastic4s.handlers.searches.queries.text

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchQuery

object MatchQueryBuilderFn {

  def apply(q: MatchQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("match").startObject(q.field)

    builder.autofield("query", q.value)

    q.queryName.foreach(builder.field("_name", _))
    q.analyzer.foreach(builder.field("analyzer", _))
    q.autoGenerateSynonymsPhraseQuery.foreach(builder.field("auto_generate_synonyms_phrase_query", _))
    q.boost.foreach(builder.field("boost", _))
    q.cutoffFrequency.map(_.toString).foreach(builder.field("cutoff_frequency", _))
    q.fuzziness.foreach(builder.field("fuzziness", _))
    q.fuzzyTranspositions.foreach(builder.field("fuzzy_transpositions", _))
    q.fuzzyRewrite.foreach(builder.field("fuzzy_rewrite", _))
    q.lenient.map(_.toString).foreach(builder.field("lenient", _))
    q.maxExpansions.foreach(builder.field("max_expansions", _))
    q.minimumShouldMatch.foreach(builder.field("minimum_should_match", _))
    q.operator.map(_.toString.toUpperCase).foreach(builder.field("operator", _))
    q.prefixLength.map(_.toString).foreach(builder.field("prefix_length", _))
    q.zeroTerms.foreach(builder.field("zero_terms_query", _))

    builder.endObject().endObject().endObject()
  }
}

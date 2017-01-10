package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.index.query.{MatchPhraseQueryBuilder, QueryBuilders}

case class MatchPhraseDefinition(field: String,
                                 value: Any,
                                 boost: Option[Double] = None,
                                 analyzer: Option[String] = None,
                                 slop: Option[Int] = None,
                                 queryName: Option[String] = None) extends QueryDefinition {

  def builder: MatchPhraseQueryBuilder = {
    val builder = QueryBuilders.matchPhraseQuery(field, value)
    analyzer.foreach(builder.analyzer)
    boost.map(_.toFloat).foreach(builder.boost)
    queryName.foreach(builder.queryName)
    slop.foreach(builder.slop)
    builder
  }

  def analyzer(a: Analyzer): MatchPhraseDefinition = copy(analyzer = Some(a.name))
  def boost(boost: Double): MatchPhraseDefinition = copy(boost = Some(boost))
  def slop(slop: Int): MatchPhraseDefinition = copy(slop = Some(slop))
  def queryName(queryName: String): MatchPhraseDefinition = copy(queryName = Some(queryName))
}

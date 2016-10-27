package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import com.sksamuel.elastic4s.analyzers.Analyzer
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

  def analyzer(a: Analyzer) = copy(analyzer = Some(a.name))
  def boost(boost: Double) = copy(boost = Some(boost))
  def slop(slop: Int) = copy(slop = Some(slop))
  def queryName(queryName: String) = copy(queryName = Some(queryName))
}

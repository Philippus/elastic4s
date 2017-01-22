package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.index.query.SimpleQueryStringFlag

case class SimpleStringQueryDefinition(query: String,
                                       analyzer: Option[String] = None,
                                       analyzeWildcard: Option[Boolean] = None,
                                       operator: Option[String] = None,
                                       queryName: Option[String] = None,
                                       lenient: Option[Boolean] = None,
                                       fields: Seq[(String, Double)] = Nil,
                                       flags: Seq[SimpleQueryStringFlag] = Nil,
                                       minimumShouldMatch: Option[Int] = None
                                      ) extends QueryDefinition {

  def flags(flags: SimpleQueryStringFlag*): SimpleStringQueryDefinition = copy(flags = flags)
  def analyzer(analyzer: String): SimpleStringQueryDefinition = copy(analyzer = analyzer.some)
  def analyzer(analyzer: Analyzer): SimpleStringQueryDefinition = copy(analyzer = analyzer.name.some)
  def queryName(queryName: String): SimpleStringQueryDefinition = copy(queryName = queryName.some)
  def defaultOperator(op: String): SimpleStringQueryDefinition = copy(operator = op.some)

  def lenient(lenient: Boolean): SimpleStringQueryDefinition = copy(lenient = lenient.some)

  def minimumShouldMatch(minimumShouldMatch: Int): SimpleStringQueryDefinition =
    copy(minimumShouldMatch = minimumShouldMatch.some)

  def analyzeWildcard(analyzeWildcard: Boolean): SimpleStringQueryDefinition =
    copy(analyzeWildcard = analyzeWildcard.some)

  def asfields(fields: String*): SimpleStringQueryDefinition = copy(fields = this.fields ++ fields.map(f => (f, -1D)))
  def field(name: String): SimpleStringQueryDefinition = copy(fields = fields :+ (name, -1D))
  def field(name: String, boost: Double): SimpleStringQueryDefinition = copy(fields = fields :+ (name, boost))
}

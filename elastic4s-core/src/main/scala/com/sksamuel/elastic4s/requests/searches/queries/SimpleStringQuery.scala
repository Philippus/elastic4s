package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.exts.OptionImplicits._

sealed trait SimpleQueryStringFlag
object SimpleQueryStringFlag {
  case object ALL        extends SimpleQueryStringFlag
  case object NONE       extends SimpleQueryStringFlag
  case object AND        extends SimpleQueryStringFlag
  case object NOT        extends SimpleQueryStringFlag
  case object OR         extends SimpleQueryStringFlag
  case object PREFIX     extends SimpleQueryStringFlag
  case object PHRASE     extends SimpleQueryStringFlag
  case object PRECEDENCE extends SimpleQueryStringFlag
  case object ESCAPE     extends SimpleQueryStringFlag
  case object WHITESPACE extends SimpleQueryStringFlag
  case object FUZZY      extends SimpleQueryStringFlag
  case object NEAR       extends SimpleQueryStringFlag
  case object SLOP       extends SimpleQueryStringFlag
}

case class SimpleStringQuery(query: String,
                             analyzer: Option[String] = None,
                             analyzeWildcard: Option[Boolean] = None,
                             operator: Option[String] = None,
                             queryName: Option[String] = None,
                             quote_field_suffix: Option[String] = None,
                             lenient: Option[Boolean] = None,
                             fields: Seq[(String, Option[Double])] = Nil,
                             flags: Seq[SimpleQueryStringFlag] = Nil,
                             minimumShouldMatch: Option[Int] = None)
    extends Query {

  def quoteFieldSuffix(suffix: String): SimpleStringQuery     = copy(quote_field_suffix = suffix.some)
  def flags(flags: SimpleQueryStringFlag*): SimpleStringQuery = copy(flags = flags)
  def analyzer(analyzer: String): SimpleStringQuery           = copy(analyzer = analyzer.some)
  def analyzer(analyzer: Analyzer): SimpleStringQuery         = copy(analyzer = analyzer.name.some)
  def queryName(queryName: String): SimpleStringQuery         = copy(queryName = queryName.some)
  def defaultOperator(op: String): SimpleStringQuery          = copy(operator = op.some)

  def lenient(lenient: Boolean): SimpleStringQuery = copy(lenient = lenient.some)

  def minimumShouldMatch(minimumShouldMatch: Int): SimpleStringQuery =
    copy(minimumShouldMatch = minimumShouldMatch.some)

  def analyzeWildcard(analyzeWildcard: Boolean): SimpleStringQuery =
    copy(analyzeWildcard = analyzeWildcard.some)

  def asfields(fields: String*): SimpleStringQuery          = copy(fields = this.fields ++ fields.map(f => (f, None)))
  def field(name: String): SimpleStringQuery                = copy(fields = fields :+ (name, None))
  def field(name: String, boost: Double): SimpleStringQuery = copy(fields = fields :+ (name, boost.some))
}

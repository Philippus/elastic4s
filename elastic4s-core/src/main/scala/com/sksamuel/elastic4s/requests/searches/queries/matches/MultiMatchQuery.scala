package com.sksamuel.elastic4s.requests.searches.queries.matches

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.common.Operator
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

sealed trait MultiMatchQueryBuilderType
object MultiMatchQueryBuilderType {

  def valueOf(str: String): MultiMatchQueryBuilderType = str.toUpperCase match {
    case "BEST_FIELDS"   => BEST_FIELDS
    case "MOST_FIELDS"   => MOST_FIELDS
    case "CROSS_FIELDS"  => CROSS_FIELDS
    case "PHRASE"        => PHRASE
    case "PHRASE_PREFIX" => PHRASE_PREFIX
  }

  case object BEST_FIELDS   extends MultiMatchQueryBuilderType
  case object MOST_FIELDS   extends MultiMatchQueryBuilderType
  case object CROSS_FIELDS  extends MultiMatchQueryBuilderType
  case object PHRASE        extends MultiMatchQueryBuilderType
  case object PHRASE_PREFIX extends MultiMatchQueryBuilderType
}

sealed trait ZeroTermsQuery
object ZeroTermsQuery {
  def valueOf(str: String): ZeroTermsQuery = str.toLowerCase match {
    case "none" => None
    case "all"  => All
  }
  case object All  extends ZeroTermsQuery
  case object None extends ZeroTermsQuery

  def ALL: All.type = All
  def NONE: None.type = None
}

case class FieldWithOptionalBoost(field: String, boost: Option[Double])

case class MultiMatchQuery(text: String,
                           analyzer: Option[String] = None,
                           cutoffFrequency: Option[Double] = None,
                           fields: Seq[FieldWithOptionalBoost] = Nil,
                           fuzziness: Option[String] = None,
                           fuzzyRewrite: Option[String] = None,
                           lenient: Option[Boolean] = None,
                           maxExpansions: Option[Int] = None,
                           minimumShouldMatch: Option[String] = None,
                           operator: Option[Operator] = None,
                           prefixLength: Option[Int] = None,
                           queryName: Option[String] = None,
                           slop: Option[Int] = None,
                           tieBreaker: Option[Double] = None,
                           `type`: Option[MultiMatchQueryBuilderType] = None,
                           zeroTermsQuery: Option[ZeroTermsQuery] = None,
                           boost: Option[Double] = None)
    extends Query {

  def fuzzyRewrite(f: String): MultiMatchQuery       = copy(fuzzyRewrite = f.some)
  def fuzziness(f: Any): MultiMatchQuery             = copy(fuzziness = f.toString.some)
  def cutoffFrequency(freq: Double): MultiMatchQuery = copy(cutoffFrequency = freq.some)
  def prefixLength(len: Int): MultiMatchQuery        = copy(prefixLength = len.some)

  def analyzer(a: Analyzer): MultiMatchQuery        = analyzer(a.name)
  def analyzer(name: String): MultiMatchQuery       = copy(analyzer = name.some)
  def queryName(queryName: String): MultiMatchQuery = copy(queryName = queryName.some)
  def boost(boost: Double): MultiMatchQuery         = copy(boost = boost.some)

  def fields(_fields: String*): MultiMatchQuery = fields(_fields.toIterable)
  def fields(_fields: Iterable[String]): MultiMatchQuery =
    copy(fields = _fields.map(FieldWithOptionalBoost(_, None)).toSeq)
  def field(name: String, boost: Double): MultiMatchQuery =
    copy(fields = fields :+ FieldWithOptionalBoost(name, boost.some))
  def fields(fields: Map[String, Double]): MultiMatchQuery =
    copy(fields = fields.map { case (f, b) => FieldWithOptionalBoost(f, b.some) }.toSeq)

  def lenient(l: Boolean): MultiMatchQuery             = copy(lenient = l.some)
  def maxExpansions(max: Int): MultiMatchQuery         = copy(maxExpansions = max.some)
  def minimumShouldMatch(min: Int): MultiMatchQuery    = minimumShouldMatch(min.toString)
  def minimumShouldMatch(min: String): MultiMatchQuery = copy(minimumShouldMatch = min.some)
  def operator(op: String): MultiMatchQuery            = copy(operator = Operator.valueOf(op).some)
  def operator(op: Operator): MultiMatchQuery          = copy(operator = op.some)
  def slop(slop: Int): MultiMatchQuery                 = copy(slop = slop.some)

  def matchType(t: String): MultiMatchQuery                     = matchType(MultiMatchQueryBuilderType.valueOf(t.toUpperCase))
  def matchType(t: MultiMatchQueryBuilderType): MultiMatchQuery = copy(`type` = t.some)

  def tieBreaker(tieBreaker: Double): MultiMatchQuery      = copy(tieBreaker = tieBreaker.some)
  def zeroTermsQuery(ztq: String): MultiMatchQuery         = copy(zeroTermsQuery = ZeroTermsQuery.valueOf(ztq).some)
  def zeroTermsQuery(ztq: ZeroTermsQuery): MultiMatchQuery = copy(zeroTermsQuery = ztq.some)
}

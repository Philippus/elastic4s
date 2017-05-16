package com.sksamuel.elastic4s.searches.queries.matches

import com.sksamuel.elastic4s.Operator
import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

sealed trait MultiMatchQueryBuilderType
object MultiMatchQueryBuilderType {

  def valueOf(str: String): MultiMatchQueryBuilderType = str.toUpperCase match {
    case "BEST_FIELDS" => BEST_FIELDS
    case "MOST_FIELDS" => MOST_FIELDS
    case "CROSS_FIELDS" => CROSS_FIELDS
    case "PHRASE" => PHRASE
    case "PHRASE_PREFIX" => PHRASE_PREFIX
  }

  case object BEST_FIELDS extends MultiMatchQueryBuilderType
  case object MOST_FIELDS extends MultiMatchQueryBuilderType
  case object CROSS_FIELDS extends MultiMatchQueryBuilderType
  case object PHRASE extends MultiMatchQueryBuilderType
  case object PHRASE_PREFIX extends MultiMatchQueryBuilderType
}

sealed trait ZeroTermsQuery
object ZeroTermsQuery {
  def valueOf(str: String): ZeroTermsQuery = str.toLowerCase match {
    case "none" => None
    case "all" => All
  }
  case object All extends ZeroTermsQuery
  case object None extends ZeroTermsQuery
}

case class MultiMatchQueryDefinition(text: String,
                                     analyzer: Option[String] = None,
                                     cutoffFrequency: Option[Double] = None,
                                     fields: Seq[(String, Float)] = Nil,
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
  extends QueryDefinition {

  def fuzzyRewrite(f: String): MultiMatchQueryDefinition = copy(fuzzyRewrite = f.some)
  def fuzziness(f: Any): MultiMatchQueryDefinition = copy(fuzziness = f.toString.some)
  def cutoffFrequency(freq: Double): MultiMatchQueryDefinition = copy(cutoffFrequency = freq.some)
  def prefixLength(len: Int): MultiMatchQueryDefinition = copy(prefixLength = len.some)

  def analyzer(a: Analyzer): MultiMatchQueryDefinition = analyzer(a.name)
  def analyzer(name: String): MultiMatchQueryDefinition = copy(analyzer = name.some)
  def queryName(queryName: String): MultiMatchQueryDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): MultiMatchQueryDefinition = copy(boost = boost.some)

  def fields(_fields: String*): MultiMatchQueryDefinition = fields(_fields.toIterable)
  def fields(_fields: Iterable[String]): MultiMatchQueryDefinition = copy(fields = _fields.map(f => (f, -1F)).toSeq)
  def field(name: String, boost: Float): MultiMatchQueryDefinition = copy(fields = fields :+ (name, boost))
  def fields(fields: Map[String, Float]): MultiMatchQueryDefinition = copy(fields = fields.toSeq)

  def lenient(l: Boolean): MultiMatchQueryDefinition = copy(lenient = l.some)
  def maxExpansions(max: Int): MultiMatchQueryDefinition = copy(maxExpansions = max.some)
  def minimumShouldMatch(min: Int): MultiMatchQueryDefinition = minimumShouldMatch(min.toString)
  def minimumShouldMatch(min: String): MultiMatchQueryDefinition = copy(minimumShouldMatch = min.some)
  def operator(op: String): MultiMatchQueryDefinition = copy(operator = Operator.valueOf(op).some)
  def operator(op: Operator): MultiMatchQueryDefinition = copy(operator = op.some)
  def slop(slop: Int): MultiMatchQueryDefinition = copy(slop = slop.some)

  def matchType(t: String): MultiMatchQueryDefinition = matchType(MultiMatchQueryBuilderType.valueOf(t.toUpperCase))
  def matchType(t: MultiMatchQueryBuilderType): MultiMatchQueryDefinition = copy(`type` = t.some)

  def tieBreaker(tieBreaker: Double): MultiMatchQueryDefinition = copy(tieBreaker = tieBreaker.some)
  def zeroTermsQuery(ztq: String): MultiMatchQueryDefinition = copy(zeroTermsQuery = ZeroTermsQuery.valueOf(ztq).some)
  def zeroTermsQuery(ztq: ZeroTermsQuery): MultiMatchQueryDefinition = copy(zeroTermsQuery = ztq.some)
}

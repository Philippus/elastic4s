package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.MatchType
import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.exts.OptionImplicits._

case class MultiMatchQueryDefinition(text: String,
                                     analyzer: Option[String] = None,
                                     queryName: Option[String] = None,
                                     cutoffFrequency: Option[Double] = None,
                                     prefixLength: Option[Int] = None,
                                     minimumShouldMatch: Option[String] = None,
                                     maxExpansions: Option[Int] = None,
                                     zeroTermsQuery: Option[String] = None,
                                     tieBreaker: Option[Double] = None,
                                     slop: Option[Int] = None,
                                     operator: Option[String] = None,
                                     lenient: Option[Boolean] = None,
                                     fields: Seq[(String, Float)] = Nil,
                                     `type`: Option[MatchType] = None,
                                     fuzziness: Option[Any] = None,
                                     fuzzyRewrite: Option[String] = None,
                                     boost: Option[Double] = None)
  extends QueryDefinition {

  def fuzzyRewrite(f: String): MultiMatchQueryDefinition = copy(fuzzyRewrite = f.some)
  def fuzziness(f: Any): MultiMatchQueryDefinition = copy(fuzziness = f.some)
  def cutoffFrequency(freq: Double): MultiMatchQueryDefinition = copy(cutoffFrequency = freq.some)
  def prefixLength(len: Int): MultiMatchQueryDefinition = copy(prefixLength = len.some)

  def analyzer(a: Analyzer): MultiMatchQueryDefinition = analyzer(a.name)
  def analyzer(name: String): MultiMatchQueryDefinition = copy(analyzer = name.some)
  def queryName(queryName: String): MultiMatchQueryDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): MultiMatchQueryDefinition = copy(boost = boost.some)

  def fields(_fields: String*): MultiMatchQueryDefinition = fields(_fields.toIterable)
  def fields(_fields: Iterable[String]) = copy(fields = _fields.map(f => (f, -1F)).toSeq)
  def field(name: String, boost: Float): MultiMatchQueryDefinition = copy(fields = fields :+ (name, boost))
  def fields(fields: Map[String, Float]): MultiMatchQueryDefinition = copy(fields = fields.toSeq)

  def lenient(l: Boolean): MultiMatchQueryDefinition = copy(lenient = l.some)
  def maxExpansions(max: Int): MultiMatchQueryDefinition = copy(maxExpansions = max.some)
  def minimumShouldMatch(min: Int): MultiMatchQueryDefinition = minimumShouldMatch(min.toString)
  def minimumShouldMatch(min: String): MultiMatchQueryDefinition = copy(minimumShouldMatch = min.some)
  def operator(op: String): MultiMatchQueryDefinition = copy(operator = op.some)
  def slop(slop: Int): MultiMatchQueryDefinition = copy(slop = slop.some)

  def matchType(t: String): MultiMatchQueryDefinition = matchType(MatchType.valueOf(t.toUpperCase))
  def matchType(t: MatchType): MultiMatchQueryDefinition =
    copy(`type` = t.some)

  def tieBreaker(tieBreaker: Double): MultiMatchQueryDefinition = copy(tieBreaker = tieBreaker.some)
  def zeroTermsQuery(zeroTermsQuery: String): MultiMatchQueryDefinition =
    copy(zeroTermsQuery = zeroTermsQuery.some)
}

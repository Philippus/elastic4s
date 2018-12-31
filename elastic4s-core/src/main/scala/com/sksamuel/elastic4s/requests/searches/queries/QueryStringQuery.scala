package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.searches.queries.matches.MultiMatchQueryBuilderType
import com.sksamuel.exts.OptionImplicits._

case class QueryStringQuery(query: String,
                            allowLeadingWildcard: Option[Boolean] = None,
                            analyzeWildcard: Option[Boolean] = None,
                            analyzer: Option[String] = None,
                            autoGeneratePhraseQueries: Option[Boolean] = None,
                            boost: Option[Double] = None,
                            defaultOperator: Option[String] = None,
                            defaultField: Option[String] = None,
                            enablePositionIncrements: Option[Boolean] = None,
                            fields: Seq[(String, Option[Double])] = Nil,
                            fuzziness: Option[String] = None,
                            fuzzyMaxExpansions: Option[Int] = None,
                            fuzzyPrefixLength: Option[Int] = None,
                            fuzzyRewrite: Option[String] = None,
                            lenient: Option[Boolean] = None,
                            minimumShouldMatch: Option[Int] = None,
                            phraseSlop: Option[Int] = None,
                            quoteFieldSuffix: Option[String] = None,
                            queryName: Option[String] = None,
                            rewrite: Option[String] = None,
                            splitOnWhitespace: Option[Boolean] = None,
                            tieBreaker: Option[Double] = None,
                            `type`: Option[MultiMatchQueryBuilderType] = None)
    extends Query {

  def rewrite(rewrite: String): QueryStringQuery = copy(rewrite = rewrite.some)
  def boost(boost: Double): QueryStringQuery     = copy(boost = boost.some)

  def analyzer(a: String): QueryStringQuery   = copy(analyzer = a.some)
  def analyzer(a: Analyzer): QueryStringQuery = analyzer(a.name)

  def defaultOperator(op: String): QueryStringQuery = copy(defaultOperator = op.some)
  def operator(op: String): QueryStringQuery        = defaultOperator(op)

  def asfields(fields: String*): QueryStringQuery = copy(fields = fields.map(f => (f, None)))

  def splitOnWhitespace(splitOnWhitespace: Boolean): QueryStringQuery =
    copy(splitOnWhitespace = splitOnWhitespace.some)

  def queryName(queryName: String): QueryStringQuery =
    copy(queryName = queryName.some)

  def fuzzyPrefixLength(fuzzyPrefixLength: Int): QueryStringQuery =
    copy(fuzzyPrefixLength = fuzzyPrefixLength.some)

  def fuzzyMaxExpansions(fuzzyMaxExpansions: Int): QueryStringQuery =
    copy(fuzzyMaxExpansions = fuzzyMaxExpansions.some)

  def fuzziness(fuzziness: String): QueryStringQuery =
    copy(fuzziness = fuzziness.some)

  def fuzzyRewrite(fuzzyRewrite: String): QueryStringQuery =
    copy(fuzzyRewrite = fuzzyRewrite.some)

  def tieBreaker(tieBreaker: Double): QueryStringQuery =
    copy(tieBreaker = tieBreaker.some)

  def allowLeadingWildcard(allowLeadingWildcard: Boolean): QueryStringQuery =
    copy(allowLeadingWildcard = allowLeadingWildcard.some)

  def lenient(lenient: Boolean): QueryStringQuery =
    copy(lenient = lenient.some)

  def minimumShouldMatch(minimumShouldMatch: Int): QueryStringQuery =
    copy(minimumShouldMatch = minimumShouldMatch.some)

  def enablePositionIncrements(enablePositionIncrements: Boolean): QueryStringQuery =
    copy(enablePositionIncrements = enablePositionIncrements.some)

  def quoteFieldSuffix(quoteFieldSuffix: String): QueryStringQuery =
    copy(quoteFieldSuffix = quoteFieldSuffix.some)

  def field(name: String): QueryStringQuery =
    copy(fields = fields :+ (name, None))

  def field(name: String, boost: Double): QueryStringQuery =
    copy(fields = fields :+ (name, Some(boost.toDouble)))

  def defaultField(field: String): QueryStringQuery =
    copy(defaultField = field.some)

  def analyzeWildcard(analyzeWildcard: Boolean): QueryStringQuery =
    copy(analyzeWildcard = analyzeWildcard.some)

  def autoGeneratePhraseQueries(autoGeneratePhraseQueries: Boolean): QueryStringQuery =
    copy(autoGeneratePhraseQueries = autoGeneratePhraseQueries.some)

  def phraseSlop(phraseSlop: Int): QueryStringQuery = copy(phraseSlop = phraseSlop.some)

  def matchType(t: String): QueryStringQuery = matchType(MultiMatchQueryBuilderType.valueOf(t))
  def matchType(t: MultiMatchQueryBuilderType): QueryStringQuery = copy(`type` = t.some)
}

package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.exts.OptionImplicits._

case class QueryStringQueryDefinition(query: String,
                                      allowLeadingWildcard: Option[Boolean] = None,
                                      analyzeWildcard: Option[Boolean] = None,
                                      analyzer: Option[String] = None,
                                      autoGeneratePhraseQueries: Option[Boolean] = None,
                                      boost: Option[Double] = None,
                                      defaultOperator: Option[String] = None,
                                      defaultField: Option[String] = None,
                                      enablePositionIncrements: Option[Boolean] = None,
                                      fields: Seq[(String, Float)] = Nil,
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
                                      tieBreaker: Option[Double] = None
                                     )
  extends QueryDefinition {

  def rewrite(rewrite: String): QueryStringQueryDefinition = copy(rewrite = rewrite.some)
  def boost(boost: Double): QueryStringQueryDefinition = copy(boost = boost.some)

  def analyzer(a: String): QueryStringQueryDefinition = copy(analyzer = a.some)
  def analyzer(a: Analyzer): QueryStringQueryDefinition = analyzer(a.name)

  def defaultOperator(op: String): QueryStringQueryDefinition = copy(defaultOperator = op.some)
  def operator(op: String): QueryStringQueryDefinition = defaultOperator(op)

  def asfields(fields: String*): QueryStringQueryDefinition = copy(fields = fields.map(f => (f, -1F)))

  def splitOnWhitespace(splitOnWhitespace: Boolean): QueryStringQueryDefinition =
    copy(splitOnWhitespace = splitOnWhitespace.some)

  def queryName(queryName: String): QueryStringQueryDefinition =
    copy(queryName = queryName.some)

  def fuzzyPrefixLength(fuzzyPrefixLength: Int): QueryStringQueryDefinition =
    copy(fuzzyPrefixLength = fuzzyPrefixLength.some)

  def fuzzyMaxExpansions(fuzzyMaxExpansions: Int): QueryStringQueryDefinition =
    copy(fuzzyMaxExpansions = fuzzyMaxExpansions.some)

  def fuzziness(fuzziness: String): QueryStringQueryDefinition =
    copy(fuzziness = fuzziness.some)

  def fuzzyRewrite(fuzzyRewrite: String): QueryStringQueryDefinition =
    copy(fuzzyRewrite = fuzzyRewrite.some)

  def tieBreaker(tieBreaker: Double): QueryStringQueryDefinition =
    copy(tieBreaker = tieBreaker.some)

  def allowLeadingWildcard(allowLeadingWildcard: Boolean): QueryStringQueryDefinition =
    copy(allowLeadingWildcard = allowLeadingWildcard.some)

  def lenient(lenient: Boolean): QueryStringQueryDefinition =
    copy(lenient = lenient.some)

  def minimumShouldMatch(minimumShouldMatch: Int): QueryStringQueryDefinition =
    copy(minimumShouldMatch = minimumShouldMatch.some)

  def enablePositionIncrements(enablePositionIncrements: Boolean): QueryStringQueryDefinition =
    copy(enablePositionIncrements = enablePositionIncrements.some)

  def quoteFieldSuffix(quoteFieldSuffix: String): QueryStringQueryDefinition =
    copy(quoteFieldSuffix = quoteFieldSuffix.some)

  def field(name: String): QueryStringQueryDefinition =
    copy(fields = fields :+ (name, -1F))

  def field(name: String, boost: Double): QueryStringQueryDefinition =
    copy(fields = fields :+ (name, boost.toFloat))

  def defaultField(field: String): QueryStringQueryDefinition =
    copy(defaultField = field.some)

  def analyzeWildcard(analyzeWildcard: Boolean): QueryStringQueryDefinition =
    copy(analyzeWildcard = analyzeWildcard.some)

  def autoGeneratePhraseQueries(autoGeneratePhraseQueries: Boolean): QueryStringQueryDefinition =
    copy(autoGeneratePhraseQueries = autoGeneratePhraseQueries.some)

  def phraseSlop(phraseSlop: Int): QueryStringQueryDefinition = copy(phraseSlop = phraseSlop.some)
}

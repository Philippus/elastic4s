package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.index.query.{Operator, QueryBuilders, QueryStringQueryBuilder}

object QueryStringBuilder {

  def builder(query: QueryStringQueryDefinition): QueryStringQueryBuilder = {
    val builder = QueryBuilders.queryStringQuery(query.query)
    query.allowLeadingWildcard.map(java.lang.Boolean.valueOf).foreach(builder.allowLeadingWildcard)
    query.analyzeWildcard.map(java.lang.Boolean.valueOf).foreach(builder.analyzeWildcard)
    query.analyzer.foreach(builder.analyzer)
    query.autoGeneratePhraseQueries.foreach(builder.autoGeneratePhraseQueries)
    query.boost.map(_.toFloat).foreach(builder.boost)
    query.defaultOperator.map(Operator.fromString).foreach(builder.defaultOperator)
    query.defaultField.foreach(builder.defaultField)
    query.enablePositionIncrements.foreach(builder.enablePositionIncrements)
    query.fields.foreach {
      case (name, -1) => builder.field(name)
      case (name, boost) => builder.field(name, boost)
    }
    query.fuzzyMaxExpansions.foreach(builder.fuzzyMaxExpansions)
    query.fuzzyPrefixLength.foreach(builder.fuzzyPrefixLength)
    query.fuzzyRewrite.foreach(builder.fuzzyRewrite)
    query.lenient.map(java.lang.Boolean.valueOf).foreach(builder.lenient)
    query.minimumShouldMatch.map(_.toString).foreach(builder.minimumShouldMatch)
    query.phraseSlop.foreach(builder.phraseSlop)
    query.quoteFieldSuffix.foreach(builder.quoteFieldSuffix)
    query.queryName.foreach(builder.queryName)
    query.rewrite.foreach(builder.rewrite)
    query.splitOnWhitespace.foreach(builder.splitOnWhitespace)
    query.tieBreaker.map(_.toFloat).foreach(builder.tieBreaker)
    builder
  }

}

case class QueryStringQueryDefinition(query: String,
                                      allowLeadingWildcard: Option[Boolean] = None,
                                      analyzeWildcard: Option[Boolean] = None,
                                      analyzer: Option[String] = None,
                                      autoGeneratePhraseQueries: Option[Boolean] = None,
                                      boost: Option[Double] = None,
                                      defaultOperator: Option[String] = None,
                                      defaultField: Option[String] = None,
                                      enablePositionIncrements: Option[Boolean] = None,
                                      fields: List[(String, Float)] = Nil,
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

  def asfields(fields: String*): QueryStringQueryDefinition = {
    fields foreach field
    this
  }

  def splitOnWhitespace(splitOnWhitespace: Boolean): QueryStringQueryDefinition =
    copy(splitOnWhitespace = splitOnWhitespace.some)

  def queryName(queryName: String): QueryStringQueryDefinition =
    copy(queryName = queryName.some)

  def fuzzyPrefixLength(fuzzyPrefixLength: Int): QueryStringQueryDefinition =
    copy(fuzzyPrefixLength = fuzzyPrefixLength.some)

  def fuzzyMaxExpansions(fuzzyMaxExpansions: Int): QueryStringQueryDefinition =
    copy(fuzzyMaxExpansions = fuzzyMaxExpansions.some)

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

package com.sksamuel.elastic4s.searches.suggestion

import com.sksamuel.exts.OptionImplicits._

sealed trait SortBy
object SortBy {
  case object Score extends SortBy
  case object Frequency extends SortBy
}

sealed trait SuggestMode
object SuggestMode {

  def valueOf(str: String): SuggestMode = str.toUpperCase match {
    case "MISSING" => Missing
    case "POPULAR" => Popular
    case "ALWAYS" => Always
  }

  case object Missing extends SuggestMode
  case object Popular extends SuggestMode
  case object Always extends SuggestMode

  def MISSING = Missing
  def POPULAR = Popular
  def ALWAYS = Always
}

sealed trait StringDistanceImpl
object StringDistanceImpl {

  def valueOf(str: String): StringDistanceImpl = str.toUpperCase match {
    case "INTERNAL" => INTERNAL
    case "DAMERAU_LEVENSHTEIN" => DAMERAU_LEVENSHTEIN
    case "LEVENSTEIN" => LEVENSTEIN
    case "JAROWINKLER" => JAROWINKLER
    case "NGRAM" => NGRAM
  }

  case object INTERNAL extends StringDistanceImpl
  case object DAMERAU_LEVENSHTEIN extends StringDistanceImpl
  case object LEVENSTEIN extends StringDistanceImpl
  case object JAROWINKLER extends StringDistanceImpl
  case object NGRAM extends StringDistanceImpl
}

case class TermSuggestionDefinition(name: String,
                                    fieldname: String,
                                    text: Option[String] = None,
                                    accuracy: Option[Double] = None,
                                    lowercaseTerms: Option[Boolean] = None,
                                    maxEdits: Option[Int] = None,
                                    maxInspections: Option[Int] = None,
                                    maxTermFreq: Option[Double] = None,
                                    minDocFreq: Option[Double] = None,
                                    minWordLength: Option[Int] = None,
                                    prefixLength: Option[Int] = None,
                                    sort: Option[SortBy] = None,
                                    stringDistance: Option[StringDistanceImpl] = None,
                                    suggestMode: Option[SuggestMode] = None,
                                    analyzer: Option[String] = None,
                                    size: Option[Int] = None,
                                    shardSize: Option[Int] = None) extends SuggestionDefinition {

  def accuracy(accuracy: Double): TermSuggestionDefinition = copy(accuracy = accuracy.some)
  def maxEdits(maxEdits: Int): TermSuggestionDefinition = copy(maxEdits = maxEdits.some)
  def maxInspections(maxInspections: Int): TermSuggestionDefinition = copy(maxInspections = maxInspections.some)
  def maxTermFreq(maxTermFreq: Double): TermSuggestionDefinition = copy(maxTermFreq = maxTermFreq.some)
  def minDocFreq(minDocFreq: Double): TermSuggestionDefinition = copy(minDocFreq = minDocFreq.some)
  def lowercaseTerms(lowercaseTerms: Boolean): TermSuggestionDefinition = copy(lowercaseTerms = lowercaseTerms.some)
  def minWordLength(minWordLength: Int): TermSuggestionDefinition = copy(minWordLength = minWordLength.some)
  def prefixLength(prefixLength: Int): TermSuggestionDefinition = copy(prefixLength = prefixLength.some)
  def sort(sort: SortBy): TermSuggestionDefinition = copy(sort = sort.some)

  def stringDistance(dist: String): TermSuggestionDefinition =
    stringDistance(StringDistanceImpl.valueOf(dist.toUpperCase))
  def stringDistance(dist: StringDistanceImpl): TermSuggestionDefinition = copy(stringDistance = dist.some)

  def mode(suggestMode: String): TermSuggestionDefinition = mode(SuggestMode.valueOf(suggestMode.toUpperCase))
  def mode(suggestMode: SuggestMode): TermSuggestionDefinition = copy(suggestMode = suggestMode.some)

  override def analyzer(analyzer: String): TermSuggestionDefinition = copy(analyzer = analyzer.some)
  override def text(text: String): TermSuggestionDefinition = copy(text = Some(text))
  override def size(size: Int): TermSuggestionDefinition = copy(size = size.some)
  override def shardSize(shardSize: Int): TermSuggestionDefinition = copy(shardSize = shardSize.some)
}

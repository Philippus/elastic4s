package com.sksamuel.elastic4s

import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.search.suggest.SuggestBuilder.SuggestionBuilder
import org.elasticsearch.search.suggest.SuggestBuilders

/** @author Stephen Samuel */
trait SuggestionDsl {

  sealed trait Suggester[S <: SuggestionDefinition]
  case object term extends Suggester[TermSuggestionDefinition]
  case object phrase extends Suggester[PhraseSuggestionDefinition]
  case object completion extends Suggester[CompletionSuggestionDefinition]
  case object fuzzyCompletion extends Suggester[FuzzyCompletionSuggestionDefinition]

  object suggest {

    class SuggestAs[S <: SuggestionDefinition](f: String => S) {
      def as(name: String): S = f(name)
    }

    def using[S <: SuggestionDefinition](suggester: Suggester[S]): SuggestAs[S] = suggester match {
      case `term` => new SuggestAs(name => new TermSuggestionDefinition(name))
      case `phrase` => new SuggestAs(name => new PhraseSuggestionDefinition(name))
      case `completion` => new SuggestAs(name => new CompletionSuggestionDefinition(name))
      case `fuzzyCompletion` => new SuggestAs(name => new FuzzyCompletionSuggestionDefinition(name))
    }

    /** used for backwards compatibility */
    def as(name: String) = using(term) as name
  }

}

trait SuggestionDefinition {
  val builder: SuggestionBuilder[_]

  def on(_text: String): this.type = text(_text)
  def text(_text: String): this.type = {
    builder.text(_text)
    this
  }

  def from(_field: String): this.type = field(_field)
  def field(_field: String): this.type = {
    builder.field(_field)
    this
  }

  def analyzer(analyzer: Analyzer): this.type = {
    builder.analyzer(analyzer.name)
    this
  }

  def size(size: Int): this.type = {
    builder.size(size)
    this
  }

  def shardSize(shardSize: Int): this.type = {
    builder.shardSize(shardSize)
    this
  }
}

class TermSuggestionDefinition(name: String) extends SuggestionDefinition {

  val builder = SuggestBuilders.termSuggestion(name)

  def maxEdits(maxEdits: Int): TermSuggestionDefinition = {
    builder.maxEdits(maxEdits)
    this
  }

  def minDocFreq(minDocFreq: Double): TermSuggestionDefinition = {
    builder.minDocFreq(minDocFreq.toFloat)
    this
  }

  def mode(suggestMode: SuggestMode): TermSuggestionDefinition = mode(suggestMode.elastic)
  def mode(suggestMode: String): TermSuggestionDefinition = {
    builder.suggestMode(suggestMode)
    this
  }

  def minWordLength(minWordLength: Int): TermSuggestionDefinition = {
    builder.minWordLength(minWordLength)
    this
  }

  def accuracy(accuracy: Double): TermSuggestionDefinition = {
    builder.setAccuracy(accuracy.toFloat)
    this
  }

  def maxInspections(maxInspections: Int): TermSuggestionDefinition = {
    builder.maxInspections(maxInspections)
    this
  }

  def maxTermFreq(maxTermFreq: Double): TermSuggestionDefinition = {
    builder.maxTermFreq(maxTermFreq.toFloat)
    this
  }

  def stringDistance(stringDistance: String): TermSuggestionDefinition = {
    builder.stringDistance(stringDistance)
    this
  }

  def prefixLength(prefixLength: Int): TermSuggestionDefinition = {
    builder.prefixLength(prefixLength)
    this
  }
}

class PhraseSuggestionDefinition(name: String) extends SuggestionDefinition {

  val builder = SuggestBuilders.phraseSuggestion(name)

  def gramSize(gramSize: Int): PhraseSuggestionDefinition = {
    builder.gramSize(gramSize)
    this
  }

  def maxErrors(maxErrors: Float): PhraseSuggestionDefinition = {
    builder.maxErrors(maxErrors)
    this
  }

  def separator(separator: String): PhraseSuggestionDefinition = {
    builder.separator(separator)
    this
  }

  def realWordErrorLikelihood(realWordErrorLikelihood: Float): PhraseSuggestionDefinition = {
    builder.realWordErrorLikelihood(realWordErrorLikelihood)
    this
  }

  def confidence(confidence: Float): PhraseSuggestionDefinition = {
    builder.confidence(confidence)
    this
  }

  def forceUnigrams(forceUnigrams: Boolean): PhraseSuggestionDefinition = {
    builder.forceUnigrams(forceUnigrams)
    this
  }

  def tokenLimit(tokenLimit: Int): PhraseSuggestionDefinition = {
    builder.tokenLimit(tokenLimit)
    this
  }

  def highlight(highlight: (String, String)): PhraseSuggestionDefinition = {
    builder.highlight(highlight._1, highlight._2)
    this
  }

}

class CompletionSuggestionDefinition(name: String) extends SuggestionDefinition {
  val builder = SuggestBuilders.completionSuggestion(name)
}

class FuzzyCompletionSuggestionDefinition(name: String) extends SuggestionDefinition {
  val builder = SuggestBuilders.fuzzyCompletionSuggestion(name)
  def fuzziness(fuzziness: Fuzziness): this.type = {
    builder.setFuzziness(fuzziness)
    this
  }
  def fuzzyMinLength(fuzzyMinLength: Int): this.type = {
    builder.setFuzzyMinLength(fuzzyMinLength)
    this
  }
  def fuzzyPrefixLength(fuzzyPrefixLength: Int): this.type = {
    builder.setFuzzyPrefixLength(fuzzyPrefixLength)
    this
  }
  def fuzzyTranspositions(fuzzyTranspositions: Boolean): this.type = {
    builder.setFuzzyTranspositions(fuzzyTranspositions)
    this
  }
  def unicodeAware(unicodeAware: Boolean): this.type = {
    builder.setUnicodeAware(unicodeAware)
    this
  }
}

sealed abstract class SuggestMode(val elastic: String)
object SuggestMode {
  case object Missing extends SuggestMode("missing")
  case object Popular extends SuggestMode("popular")
  case object Always extends SuggestMode("always")
}

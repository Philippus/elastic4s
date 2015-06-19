package com.sksamuel.elastic4s

import org.elasticsearch.action.suggest.SuggestResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.search.suggest.SuggestBuilder.SuggestionBuilder
import org.elasticsearch.search.suggest.{Suggest, SuggestBuilders}

import scala.concurrent.Future

/** @author Stephen Samuel */
trait SuggestionDsl {

  sealed trait Suggester[S <: SuggestionDefinition]
  case object term extends Suggester[TermSuggestionDefinition]
  case object phrase extends Suggester[PhraseSuggestionDefinition]
  case object completion extends Suggester[CompletionSuggestionDefinition]
  case object fuzzyCompletion extends Suggester[FuzzyCompletionSuggestionDefinition]

  implicit object SuggestionsDefinitionExecutable
    extends Executable[TermSuggestionDefinition, SuggestResponse, SuggestResult] {
    override def apply(client: Client, t: TermSuggestionDefinition): Future[SuggestResult] = {
      val req = client.prepareSuggest(t.indexes: _*)
      req.addSuggestion(t.builder)
      injectFutureAndMap(req.execute) { resp =>
        SuggestResult(resp.getSuggest)
      }
    }
  }

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

class TermSuggestionDefinition(val name: String, val indexes: Seq[String] = Nil) extends SuggestionDefinition {

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

  /**
   * The minimum length a suggest text term must have in order to be corrected.
   */
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

case class SuggestResult(suggestions: Array[Suggestion], suggest: org.elasticsearch.search.suggest.Suggest)

object SuggestResult {

  import scala.collection.JavaConverters._

  def apply(suggest: Suggest): SuggestResult = {
    val suggestions = suggest.iterator.asScala.map { sugg =>
      Suggestion(sugg.getType, sugg.getName, sugg.getEntries.asScala.map { ent =>
        SuggestionEntry(ent.getLength, ent.getOffset, ent.getText.string, ent.getOptions.asScala.map { opt =>
          SuggestionOption(
            opt.getText.string,
            opt.getScore,
            Option(opt.getHighlighted).map(_.toString),
            opt.collateMatch
          )
        }.toArray)
      }.toArray)
    }.toArray
    SuggestResult(suggestions, suggest)
  }
}

case class Suggestion(`type`: Int, name: String, entries: Array[SuggestionEntry]) {
  def size = entries.length
  def entry(term: String): SuggestionEntry = entries.find(_.term == term).get
  def entryTerms: Array[String] = entries.map(_.term)
}

case class SuggestionEntry(length: Int, offset: Int, term: String, options: Array[SuggestionOption]) {
  def hasSuggestions = !options.isEmpty
  def optionsText: Array[String] = options.map(_.text)
}

case class SuggestionOption(text: String, score: Double, highlighted: Option[String], collateMatch: Boolean)
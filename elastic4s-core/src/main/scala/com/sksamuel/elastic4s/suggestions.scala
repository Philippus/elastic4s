package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.anaylzers.Analyzer
import org.elasticsearch.action.suggest.SuggestResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.search.suggest.Suggest.Suggestion
import org.elasticsearch.search.suggest.SuggestBuilder.SuggestionBuilder
import org.elasticsearch.search.suggest.completion.{CompletionSuggestion, CompletionSuggestionBuilder, CompletionSuggestionFuzzyBuilder}
import org.elasticsearch.search.suggest.phrase.{PhraseSuggestion, PhraseSuggestionBuilder}
import org.elasticsearch.search.suggest.term.{TermSuggestion, TermSuggestionBuilder}
import org.elasticsearch.search.suggest.{Suggest, SuggestBuilders}

import scala.collection.JavaConverters._
import scala.concurrent.Future

/** @author Stephen Samuel */
trait SuggestionDsl {

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
}

case class SuggestDefinition(suggestions: Seq[SuggestionDefinition])

trait SuggestionDefinition {
  type B <: SuggestionBuilder[B]
  type R <: SuggestionResult

  val name: String
  val builder: SuggestionBuilder[B]

  @deprecated("use text", "1.6.1")
  def on(_text: String): this.type = text(_text)
  def text(_text: String): this.type = {
    builder.text(_text)
    this
  }

  @deprecated("use field", "1.6.1")
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

  def context(field: String, values: String*): this.type = {
    builder.addContextField(field, values.asJava)
    this
  }

  def context(field: String, values: Iterable[String]): this.type = {
    builder.addContextField(field, values.asJava)
    this
  }
}

case class TermSuggestionDefinition(name: String, indexes: Seq[String] = Nil)
  extends SuggestionDefinition {
  override type B = TermSuggestionBuilder
  override type R <: TermSuggestionResult

  override val builder = SuggestBuilders.termSuggestion(name)

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

case class PhraseSuggestionDefinition(name: String) extends SuggestionDefinition {
  override type B = PhraseSuggestionBuilder
  override type R <: PhraseSuggestionResult

  override val builder = SuggestBuilders.phraseSuggestion(name)

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

case class CompletionSuggestionDefinition(name: String) extends SuggestionDefinition {
  override type B = CompletionSuggestionBuilder
  override type R <: CompletionSuggestionResult

  override val builder = SuggestBuilders.completionSuggestion(name)
}

case class FuzzyCompletionSuggestionDefinition(name: String)
  extends SuggestionDefinition {

  override type B = CompletionSuggestionFuzzyBuilder
  override val builder = SuggestBuilders.fuzzyCompletionSuggestion(name)

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

case class SuggestResult(suggestions: Seq[SuggestionResult],
                         suggest: org.elasticsearch.search.suggest.Suggest) {
  def suggestion(name: String): SuggestionResult = suggestions.find(_.name == name).get
  def suggestion(d: SuggestionDefinition): d.R = suggestion(d.name).asInstanceOf[d.R]
}

object SuggestResult {
  def apply(suggest: Suggest): SuggestResult = {
    val suggestions = suggest.iterator.asScala.map(SuggestionResult.apply).toSeq
    SuggestResult(suggestions, suggest)
  }
}

// scala version of Suggest.Suggestion
trait SuggestionResult {
  type R <: Suggestion[_]
  type E <: SuggestionEntry
  def suggestion: R
  def name = suggestion.getName
  def size: Int = suggestion.getEntries.size
  def `type`: Int = suggestion.getType
  def entries: Seq[E]
  def entry(term: String): SuggestionEntry = entries.find(_.term == term).get
  def entryTerms: Seq[String] = entries.map(_.term)
}

object SuggestionResult {
  def apply(suggestion: Suggest.Suggestion[_ <: Suggestion.Entry[_]]): SuggestionResult = suggestion match {
    case t: TermSuggestion => TermSuggestionResult(t)
    case p: PhraseSuggestion => PhraseSuggestionResult(p)
    case c: CompletionSuggestion => CompletionSuggestionResult(c)
  }
}

case class TermSuggestionResult(suggestion: TermSuggestion) extends SuggestionResult {
  type R = TermSuggestion
  type E = TermSuggestionEntry
  def entries: Seq[TermSuggestionEntry] = suggestion.getEntries.asScala.map(TermSuggestionEntry).toSeq
}

case class PhraseSuggestionResult(suggestion: PhraseSuggestion) extends SuggestionResult {
  type R = PhraseSuggestion
  type E = PhraseSuggestionEntry
  def entries: Seq[PhraseSuggestionEntry] = suggestion.getEntries.asScala.map(PhraseSuggestionEntry).toSeq
}

case class CompletionSuggestionResult(suggestion: CompletionSuggestion) extends SuggestionResult {
  type R = CompletionSuggestion
  type E = CompletionSuggestionEntry
  def entries: Seq[CompletionSuggestionEntry] = suggestion.getEntries.asScala.map(CompletionSuggestionEntry).toSeq
}

// scala version of Suggest.Suggestion.Entry
trait SuggestionEntry {
  type R <: Suggestion.Entry[_]
  def entry: R
  def length: Int = entry.getLength
  def term: String = entry.getText.string
  def offset: Int = entry.getOffset
  def isEmpty: Boolean = options.isEmpty
  def nonEmpty: Boolean = options.nonEmpty
  def optionsText: Seq[String] = options.map(_.text)
  def options: Seq[SuggestionOption] = entry
    .getOptions
    .asScala
    .map(arg => SuggestionOption.apply(arg.asInstanceOf[Suggestion.Entry.Option]))
    .toSeq
}

case class TermSuggestionEntry(entry: TermSuggestion.Entry) extends SuggestionEntry {
  type R = TermSuggestion.Entry
}

case class PhraseSuggestionEntry(entry: PhraseSuggestion.Entry) extends SuggestionEntry {
  type R = PhraseSuggestion.Entry
  def cutoffScore = entry.getCutoffScore
}

case class CompletionSuggestionEntry(entry: CompletionSuggestion.Entry) extends SuggestionEntry {
  type R = CompletionSuggestion.Entry
}

// scala version of Suggest.Suggestion.Entry.Option
case class SuggestionOption(text: String, score: Double, highlighted: Option[String], collateMatch: Boolean)

object SuggestionOption {
  def apply(option: Suggestion.Entry.Option): SuggestionOption = {
    SuggestionOption(
      option.getText.string,
      option.getScore,
      Option(option.getHighlighted).map(_.string),
      option.collateMatch
    )
  }
}

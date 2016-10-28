package com.sksamuel.elastic4s.search.suggestions

import org.elasticsearch.search.suggest.Suggest.Suggestion
import org.elasticsearch.search.suggest.completion.CompletionSuggestion
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion
import org.elasticsearch.search.suggest.term.TermSuggestion
import org.elasticsearch.search.suggest.{Suggest, SuggestionBuilder}

import scala.collection.JavaConverters._

case class SuggestDefinition(suggestions: Seq[SuggestionDefinition])

trait SuggestionDefinition {

  type B <: SuggestionBuilder[B]

  def builder: B

  val fieldname: String

  def size: Option[Int]
  def shardSize: Option[Int]
  def text: Option[String]
  def analyzer: Option[String]

  def size(size: Int): SuggestionDefinition
  def shardSize(shardSize: Int): SuggestionDefinition
  def text(text: String): SuggestionDefinition
  def analyzer(analyzer: String): SuggestionDefinition

  def populate(builder: B): Unit = {
    analyzer.foreach(builder.analyzer)
    shardSize.foreach(builder.shardSize(_))
    size.foreach(builder.size)
    text.foreach(builder.text)
  }
}

case class SuggestResult(suggestions: Seq[SuggestionResult],
                         suggest: org.elasticsearch.search.suggest.Suggest) {
  def suggestion(name: String): SuggestionResult = suggestions.find(_.name == name).get
  //def suggestion(d: SuggestionDefinition): d.R = suggestion(d.fieldname).asInstanceOf[d.R]
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

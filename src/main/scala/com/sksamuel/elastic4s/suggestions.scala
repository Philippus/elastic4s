package com.sksamuel.elastic4s

import org.elasticsearch.search.suggest.SuggestBuilder
import org.elasticsearch.search.suggest.SuggestBuilder.SuggestionBuilder

/** @author Stephen Samuel */
trait SuggestionDsl {
  def suggest = new SuggestionExpectsAs
  class SuggestionExpectsAs {
    def as(name: String) = new SuggestionExpectsText(name)
  }
  def suggest(name: String) = new SuggestionExpectsText(name)
  class SuggestionExpectsText(name: String) {
    def on(text: String) = new TermSuggestionDefinition(name)
    def as = this
  }
}

trait SuggestionDefinition {
  val builder: SuggestionBuilder[_]
}
class TermSuggestionDefinition(name: String) extends SuggestionDefinition {

  val builder = SuggestBuilder.termSuggestion(name)

  def from(f: String) = field(f)
  def field(f: String) = {
    builder.field(f)
    this
  }
  def maxEdits(maxEdits: Int): TermSuggestionDefinition = {
    builder.maxEdits(maxEdits)
    this
  }
  def minDocFreq(minDocFreq: Double): TermSuggestionDefinition = {
    builder.minDocFreq(minDocFreq.toFloat)
    this
  }
  def analyzer(analyzer: Analyzer): TermSuggestionDefinition = {
    builder.analyzer(analyzer.elastic)
    this
  }
  def size(size: Int): TermSuggestionDefinition = {
    builder.size(size)
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
  def shardSize(shardSize: Int): TermSuggestionDefinition = {
    builder.shardSize(shardSize)
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

sealed abstract class SuggestMode(val elastic: String)
object SuggestMode {
  case object Missing extends SuggestMode("missing")
  case object Popular extends SuggestMode("popular")
  case object Always extends SuggestMode("always")
}
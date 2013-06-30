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
    def minDocFreq(minDocFreq: Double) = {
        builder.minDocFreq(minDocFreq.toFloat)
        this
    }
    def analyzer(analyzer: Analyzer) = {
        builder.analyzer(analyzer.elastic)
        this
    }
    def size(size: Int) = {
        builder.size(size)
        this
    }
    def mode(suggestMode: String) = {
        builder.suggestMode(suggestMode)
        this
    }
    def mode(suggestMode: SuggestMode) = {
        builder.suggestMode(suggestMode.elastic)
        this
    }
    def minWordLength(minWordLength: Int) = {
        builder.minWordLength(minWordLength)
        this
    }
    def shardSize(shardSize: Int) = {
        builder.shardSize(shardSize)
        this
    }
    def accuracy(accuracy: Double) = {
        builder.setAccuracy(accuracy.toFloat)
        this
    }
    def maxInspections(maxInspections: Int) = {
        builder.maxInspections(maxInspections)
        this
    }
    def maxTermFreq(maxTermFreq: Double) = {
        builder.maxTermFreq(maxTermFreq.toFloat)
        this
    }
    def stringDistance(stringDistance: String) = {
        builder.stringDistance(stringDistance)
        this
    }
    def prefixLength(prefixLength: Int) = {
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
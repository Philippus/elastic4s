package com.sksamuel.elastic4s.requests.searches

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.HitReader
import com.sksamuel.elastic4s.requests.common.Shards
import com.sksamuel.elastic4s.requests.searches.aggs.responses.Aggregations
import com.sksamuel.elastic4s.requests.searches.suggestion.{CompletionSuggestionResult, PhraseSuggestionResult, SuggestionResult, TermSuggestionResult}

import scala.reflect.ClassTag
import scala.util.Try

case class SearchResponse(took: Long,
                          @JsonProperty("timed_out") isTimedOut: Boolean,
                          @JsonProperty("terminated_early") isTerminatedEarly: Boolean,
                          private val suggest: Map[String, Seq[SuggestionResult]],
                          @JsonProperty("_shards") private val _shards: Shards,
                          @JsonProperty("_scroll_id") scrollId: Option[String],
                          @JsonProperty("aggregations") private val _aggregationsAsMap: Map[String, Any],
                          hits: SearchHits) {

  def aggregationsAsMap: Map[String, Any] = Option(_aggregationsAsMap).getOrElse(Map.empty)
  def totalHits: Long = hits.total.value
  def size: Long = hits.size
  def ids: Seq[String] = hits.hits.map(_.id)
  def maxScore: Double = hits.maxScore

  def shards: Shards = Option(_shards).getOrElse(Shards(-1, -1, -1))

  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty

//  lazy val aggsAsContentBuilder = SourceAsContentBuilder(aggregationsAsMap)
//  lazy val aggregationsAsString: String = aggsAsContentBuilder.string()
  def aggs: Aggregations = aggregations
  def aggregations: Aggregations = Aggregations(aggregationsAsMap)

  def suggestions: Map[String, Seq[SuggestionResult]] = Option(suggest).getOrElse(Map.empty)

  private def suggestion(name: String): Map[String, SuggestionResult] =
    suggestions
      .getOrElse(name, Nil)
      .map { result =>
        result.text -> result
      }
      .toMap

  def termSuggestion(name: String): Map[String, TermSuggestionResult] = suggestion(name).mapValues(_.toTerm)
  def completionSuggestion(name: String): Map[String, CompletionSuggestionResult] = suggestion(name).mapValues(_.toCompletion)
  def phraseSuggestion(name: String): Map[String, PhraseSuggestionResult] = suggestion(name).mapValues(_.toPhrase)

  def to[T: HitReader : ClassTag]: IndexedSeq[T] = hits.hits.map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Try[T]] = hits.hits.map(_.safeTo[T]).toIndexedSeq
}

package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.{HitAs, HitReader}
import org.elasticsearch.action.search.{SearchResponse, ShardSearchFailure}
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.aggregations.Aggregations

import scala.concurrent.duration._

case class RichSearchResponse(original: SearchResponse) {

  @deprecated("use resp.aggregations, or resp.original.getAggregations", "2.0.0")
  def getAggregations = original.getAggregations

  // java aliases
  @deprecated("use suggest", "3.0.0")
  def getSuggest = original.getSuggest

  @deprecated("use scrollId or scrollIdOpt", "3.0.0")
  def getScrollId: String = original.getScrollId

  @deprecated("use hits", "3.0.0")
  def getHits: SearchHits = original.getHits

  @deprecated("use took", "3.0.0")
  def getTook = original.getTook

  @deprecated("use tookInMillis", "3.0.0")
  def getTookInMillis = original.getTookInMillis

  def size: Int = original.getHits.hits().length
  def ids: Seq[String] = hits.map(_.id)
  def totalHits: Long = original.getHits.getTotalHits
  def maxScore: Float = original.getHits.getMaxScore

  def hits: Array[RichSearchHit] = original.getHits.getHits.map(RichSearchHit.apply)

  @deprecated("use to[T], which uses a Reader typeclass", "3.0.0")
  def as[T](implicit hitas: HitAs[T], manifest: Manifest[T]): Array[T] = hits.map(_.as[T])

  def to[T](implicit reader: HitReader[T], manifest: Manifest[T]) = hits.map(_.to[T])

  def scrollId: String = original.getScrollId
  def scrollIdOpt: Option[String] = Option(scrollId)

  def totalShards: Int = original.getTotalShards
  def successfulShards: Int = original.getSuccessfulShards
  def shardFailures: Array[ShardSearchFailure] = Option(original.getShardFailures).getOrElse(Array.empty)

  def tookInMillis: Long = original.getTookInMillis
  def took: Duration = original.getTookInMillis.millis

  def aggregations: Aggregations = original.getAggregations

  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty

  //  def suggest: SuggestResult = SuggestResult(original.getSuggest)
  //  def suggestions = suggest.suggestions
  //  def suggestion(name: String): SuggestionResult = suggest.suggestions.find(_.name == name).get
  //  def suggestion[A](sd: SuggestionDefinition): sd.R = suggestion(sd.name).asInstanceOf[sd.R]

  def isTimedOut: Boolean = original.isTimedOut
  def isTerminatedEarly: Boolean = original.isTerminatedEarly
}

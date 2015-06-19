package com.sksamuel.elastic4s

import org.apache.lucene.search.Explanation
import org.elasticsearch.action.search.{SearchResponse, ShardSearchFailure}
import org.elasticsearch.common.bytes.BytesReference
import org.elasticsearch.search.aggregations.Aggregations
import org.elasticsearch.search.facet.Facets
import org.elasticsearch.search.highlight.HighlightField
import org.elasticsearch.search.{SearchHit, SearchHitField, SearchHits, SearchShardTarget}

import scala.concurrent.duration._

class RichSearchResponse(resp: SearchResponse) {

  def totalHits: Long = resp.getHits.getTotalHits
  def maxScore: Float = resp.getHits.getMaxScore
  def hits: Array[RichSearchHit] = resp.getHits.getHits.map(new RichSearchHit(_))
  def hitsAs[T](implicit reader: Reader[T], manifest: Manifest[T]): Array[T] = hits.map(_.mapTo[T])

  def scrollId: String = resp.getScrollId

  def totalShards: Int = resp.getTotalShards
  def successfulShards: Int = resp.getSuccessfulShards
  def shardFailures: Array[ShardSearchFailure] = Option(resp.getShardFailures).getOrElse(Array.empty)

  def tookInMillis: Long = resp.getTookInMillis
  def took: Duration = resp.getTookInMillis.millis

  def facets: Facets = resp.getFacets
  def aggregations: Aggregations = resp.getAggregations

  def suggest: SuggestResult = SuggestResult(resp.getSuggest)
  def suggestion(name: String) = suggest.suggestions.find(_.name == name)

  def isTimedOut: Boolean = resp.isTimedOut
  def isTerminatedEarly: Boolean = resp.isTerminatedEarly
}

class RichSearchHit(hit: SearchHit) {

  import scala.collection.JavaConverters._

  def id: String = hit.id
  def index: String = hit.index
  def `type`: String = hit.`type`()
  def score: Float = hit.score
  def nestedIdentity: SearchHit.NestedIdentity = hit.getNestedIdentity
  def version: Long = hit.version()
  def shard: SearchShardTarget = hit.shard

  def sourceRef: BytesReference = hit.sourceRef()
  def source: Array[Byte] = Option(hit.source).getOrElse(Array.emptyByteArray)
  def isSourceEmpty: Boolean = hit.isSourceEmpty
  def sourceAsString: String = Option(hit.sourceAsString).getOrElse("")
  def sourceAsMap: Map[String, AnyRef] = Option(hit.sourceAsMap).map(_.asScala.toMap).getOrElse(Map.empty)
  def mapTo[T](implicit reader: Reader[T], manifest: Manifest[T]): T = reader.read(sourceAsString)

  def explanation: Option[Explanation] = Option(hit.explanation)

  def field(fieldName: String): SearchHitField = fieldOpt(fieldName).get
  def fieldOpt(fieldName: String): Option[SearchHitField] = Option(hit.field(fieldName))
  def fields: Map[String, SearchHitField] = Option(hit.fields).map(_.asScala.toMap).getOrElse(Map.empty)

  def highlightFields: Map[String, HighlightField] = {
    Option(hit.highlightFields).map(_.asScala.toMap).getOrElse(Map.empty)
  }

  def sortValues: Array[AnyRef] = Option(hit.sortValues).getOrElse(Array.empty)
  def matchedQueries: Array[String] = Option(hit.matchedQueries).getOrElse(Array.empty)
  def innerHits: Map[String, SearchHits] = Option(hit.getInnerHits).map(_.asScala.toMap).getOrElse(Map.empty)
}


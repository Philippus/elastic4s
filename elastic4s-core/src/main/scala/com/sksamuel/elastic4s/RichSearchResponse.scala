package com.sksamuel.elastic4s

import org.apache.lucene.search.Explanation
import org.elasticsearch.action.search.{SearchResponse, ShardSearchFailure}
import org.elasticsearch.common.bytes.BytesReference
import org.elasticsearch.search.aggregations.Aggregations
import org.elasticsearch.search.facet.Facets
import org.elasticsearch.search.highlight.HighlightField
import org.elasticsearch.search.{SearchHit, SearchHitField, SearchHits, SearchShardTarget}

import scala.concurrent.duration._

case class RichSearchResponse(original: SearchResponse) extends AnyVal {

  def totalHits: Long = original.getHits.getTotalHits
  def maxScore: Float = original.getHits.getMaxScore

  def hits: Array[RichSearchHit] = original.getHits.getHits.map(new RichSearchHit(_))

  @deprecated("use as[T], which has a more powerful typeclass abstraction", "1.6.1")
  def hitsAs[T](implicit reader: Reader[T], manifest: Manifest[T]): Array[T] = hits.map(_.mapTo[T])
  def as[T](implicit hitas: HitAs[T], manifest: Manifest[T]): Array[T] = hits.map(_.as[T])

  def scrollId: String = original.getScrollId

  def totalShards: Int = original.getTotalShards
  def successfulShards: Int = original.getSuccessfulShards
  def shardFailures: Array[ShardSearchFailure] = Option(original.getShardFailures).getOrElse(Array.empty)

  def tookInMillis: Long = original.getTookInMillis
  def took: Duration = original.getTookInMillis.millis

  def facets: Facets = original.getFacets
  def aggregations: Aggregations = original.getAggregations

  def isEmpty: Boolean = hits.isEmpty

  def suggest: SuggestResult = SuggestResult(original.getSuggest)
  def suggestions = suggest.suggestions
  def suggestion(name: String): SuggestionResult = suggest.suggestions.find(_.name == name).get
  def suggestion[A](sd: SuggestionDefinition): sd.R = suggestion(sd.name).asInstanceOf[sd.R]

  def isTimedOut: Boolean = original.isTimedOut
  def isTerminatedEarly: Boolean = original.isTerminatedEarly
}

class RichSearchHit(hit: SearchHit) {

  override def equals(other: Any): Boolean = other match {
    case hit: SearchHit => equals(new RichSearchHit(hit))
    case hit: RichSearchHit =>
      this.index == hit.index && this.`type` == hit.`type` && this.sourceAsString == hit.sourceAsString
    case _ => false
  }

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

  @deprecated("use as[T], which has a more powerful typeclass abstraction", "1.6.1")
  def mapTo[T](implicit reader: Reader[T], manifest: Manifest[T]): T = reader.read(sourceAsString)
  def as[T](implicit hitas: HitAs[T], manifest: Manifest[T]): T = hitas.as(this)

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


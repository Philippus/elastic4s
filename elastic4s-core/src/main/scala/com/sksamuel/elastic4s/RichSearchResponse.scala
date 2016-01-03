package com.sksamuel.elastic4s

import org.apache.lucene.search.Explanation
import org.elasticsearch.action.search.{SearchResponse, ShardSearchFailure}
import org.elasticsearch.common.bytes.BytesReference
import org.elasticsearch.search.aggregations.Aggregations
import org.elasticsearch.search.highlight.HighlightField
import org.elasticsearch.search.{SearchHit, SearchHitField, SearchHits, SearchShardTarget}

import scala.concurrent.duration._

case class RichSearchResponse(original: SearchResponse) {

  @deprecated("use resp.aggregations, or resp.original.getAggregations", "2.0.0")
  def getAggregations = original.getAggregations

  // java aliases
  def getSuggest = original.getSuggest
  def getScrollId: String = original.getScrollId
  def getHits: SearchHits = original.getHits
  def getTook = original.getTook
  def getTookInMillis = original.getTookInMillis

  def totalHits: Long = original.getHits.getTotalHits
  def maxScore: Float = original.getHits.getMaxScore

  def hits: Array[RichSearchHit] = original.getHits.getHits.map(RichSearchHit.apply)

  @deprecated("use as[T], which handles errors", "1.6.1")
  def hitsAs[T](implicit reader: Reader[T], manifest: Manifest[T]): Array[T] = hits.map(_.mapTo[T])

  def as[T](implicit hitas: HitAs[T], manifest: Manifest[T]): Array[T] = hits.map(_.as[T])

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

  def suggest: SuggestResult = SuggestResult(original.getSuggest)
  def suggestions = suggest.suggestions
  def suggestion(name: String): SuggestionResult = suggest.suggestions.find(_.name == name).get
  def suggestion[A](sd: SuggestionDefinition): sd.R = suggestion(sd.name).asInstanceOf[sd.R]

  def isTimedOut: Boolean = original.isTimedOut
  def isTerminatedEarly: Boolean = original.isTerminatedEarly
}

case class RichSearchHit(java: SearchHit) {

  override def equals(other: Any): Boolean = other match {
    case hit: SearchHit => equals(new RichSearchHit(hit))
    case hit: RichSearchHit =>
      this.index == hit.index && this.`type` == hit.`type` && this.sourceAsString == hit.sourceAsString
    case _ => false
  }

  import scala.collection.JavaConverters._

  // java method aliases
  @deprecated("use .java", "2.1.2")
  def hit = java
  def getId: String = java.getId
  def getIndex: String = index
  def getType: String = `type`
  def getVersion: Long = version
  def getSource = java.getSource
  def getSourceAsString = java.getSourceAsString
  def getScore = java.getScore
  def getShard = java.getShard

  def id: String = java.id
  def index: String = java.index
  def `type`: String = java.`type`()

  def score: Float = java.score
  def nestedIdentity: SearchHit.NestedIdentity = java.getNestedIdentity
  def version: Long = java.version()
  def shard: SearchShardTarget = java.shard

  def sourceRef: BytesReference = java.sourceRef()
  def source: Array[Byte] = Option(java.source).getOrElse(Array.emptyByteArray)
  def isSourceEmpty: Boolean = java.isSourceEmpty
  def sourceAsString: String = Option(java.sourceAsString).getOrElse("")
  def sourceAsMap: Map[String, AnyRef] = Option(java.sourceAsMap).map(_.asScala.toMap).getOrElse(Map.empty)

  @deprecated("use as[T]", "2.0.0")
  def mapTo[T](implicit reader: Reader[T], manifest: Manifest[T]): T = reader.read(sourceAsString)

  def as[T](implicit hitas: HitAs[T], manifest: Manifest[T]): T = hitas.as(this)

  def explanation: Option[Explanation] = Option(java.explanation)

  def fields: Map[String, RichSearchHitField] = {
    Option(java.fields).map(_.asScala.toMap.mapValues(RichSearchHitField)).getOrElse(Map.empty)
  }

  def field(fieldName: String): RichSearchHitField = fields(fieldName)
  def fieldOpt(fieldName: String): Option[RichSearchHitField] = fields.get(fieldName)
  def fieldsSeq: Seq[RichSearchHitField] = fields.values.toSeq

  def highlightFields: Map[String, HighlightField] = {
    Option(java.highlightFields).map(_.asScala.toMap).getOrElse(Map.empty)
  }

  def sortValues: Array[AnyRef] = Option(java.sortValues).getOrElse(Array.empty)
  def matchedQueries: Array[String] = Option(java.matchedQueries).getOrElse(Array.empty)
  def innerHits: Map[String, SearchHits] = Option(java.getInnerHits).map(_.asScala.toMap).getOrElse(Map.empty)
}

case class RichSearchHitField(java: SearchHitField) extends AnyVal {

  @deprecated("use .java", "2.1.2")
  def value = java

  // java method aliases
  def getName: String = name
  def getValue[V]: V = value[V]
  def getValues: Seq[AnyRef] = values

  import scala.collection.JavaConverters._

  def name: String = java.name()
  def value[V]: V = java.getValue[V]
  def values: Seq[AnyRef] = java.values().asScala.toList
  def isMetadataField: Boolean = java.isMetadataField
}
package com.sksamuel.elastic4s

import org.apache.lucene.search.Explanation
import org.elasticsearch.action.search.{SearchResponse, ShardSearchFailure}
import org.elasticsearch.common.bytes.BytesReference
import org.elasticsearch.search.aggregations.Aggregations
import org.elasticsearch.search.highlight.HighlightField
import org.elasticsearch.search.{SearchHit, SearchHitField, SearchHits, SearchShardTarget}
import org.scalactic.{Or, Every, ErrorMessage}

import scala.concurrent.duration._

case class RichSearchResponse(original: SearchResponse) extends AnyVal {

  def totalHits: Long = original.getHits.getTotalHits
  def maxScore: Float = original.getHits.getMaxScore

  @deprecated("use hits() to get scala wrappers, or response.original.getHits to get the raw SearchHits", "2.0.0")
  def getHits: SearchHits = original.getHits
  def hits: Array[RichSearchHit] = original.getHits.getHits.map(new RichSearchHit(_))

  @deprecated("use readAs[T], which handles errors", "1.6.1")
  def hitsAs[T](implicit reader: Reader[T], manifest: Manifest[T]): Array[T] = hits.map(_.mapTo[T])
  @deprecated("use readAs[T], which handles errors", "1.6.1")
  def as[T](implicit hitas: HitAs[T], manifest: Manifest[T]): Array[T] = hits.map(_.as[T])

  def readAs[T](implicit reader: HitReader[T]): Seq[T Or Every[ErrorMessage]] = hits.map(_.readAs[T])

  @deprecated("use resp.aggregations, or resp.original.getAggregations", "2.0.0")
  def getAggregations = original.getAggregations

  @deprecated("use resp.suggest, or resp.original.getSuggest", "2.0.0")
  def getSuggest = original.getSuggest

  @deprecated("use scrollId", "2.0.0")
  def getScrollId: String = scrollId
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

case class RichSearchHit(hit: SearchHit) {

  override def equals(other: Any): Boolean = other match {
    case hit: SearchHit => equals(new RichSearchHit(hit))
    case hit: RichSearchHit =>
      this.index == hit.index && this.`type` == hit.`type` && this.sourceAsString == hit.sourceAsString
    case _ => false
  }

  import scala.collection.JavaConverters._

  lazy val id: String = hit.id
  @deprecated("use id", "2.0.0")
  lazy val getId: String = hit.getId

  lazy val index: String = hit.index
  @deprecated("use index", "2.0.0")
  lazy val getIndex: String = index

  lazy val `type`: String = hit.`type`()
  @deprecated("use `type", "2.0.0")
  lazy val getType: String = `type`

  lazy val score: Float = hit.score
  lazy val nestedIdentity: SearchHit.NestedIdentity = hit.getNestedIdentity
  lazy val version: Long = hit.version()
  lazy val shard: SearchShardTarget = hit.shard

  lazy val sourceRef: BytesReference = hit.sourceRef()
  lazy val source: Array[Byte] = Option(hit.source).getOrElse(Array.emptyByteArray)
  lazy val isSourceEmpty: Boolean = hit.isSourceEmpty
  lazy val sourceAsString: String = Option(hit.sourceAsString).getOrElse("")
  def sourceAsMap: Map[String, AnyRef] = Option(hit.sourceAsMap).map(_.asScala.toMap).getOrElse(Map.empty)

  def richFields: RichSearchHitFields = {
    RichSearchHitFields(sourceAsMap.map { case (k, v) => (k, SomeValueSearchHitField(k, v)) })
  }

  @deprecated("use as[T], which handles errors", "2.0.0")
  def mapTo[T](implicit reader: Reader[T], manifest: Manifest[T]): T = reader.read(sourceAsString)

  @deprecated("use readAs[T], which handles errors", "2.0.0")
  def as[T](implicit hitas: HitAs[T], manifest: Manifest[T]): T = hitas.as(this)

  def readAs[T](implicit reader: HitReader[T]): T Or Every[ErrorMessage] = reader.as(this)

  lazy val explanation: Option[Explanation] = Option(hit.explanation)

  def field(fieldName: String): SearchHitField = fieldOpt(fieldName).get
  def fieldOpt(fieldName: String): Option[SearchHitField] = Option(hit.field(fieldName))
  def fields: Map[String, SearchHitField] = Option(hit.fields).map(_.asScala.toMap).getOrElse(Map.empty)

  lazy val highlightFields: Map[String, HighlightField] = {
    Option(hit.highlightFields).map(_.asScala.toMap).getOrElse(Map.empty)
  }

  lazy val sortValues: Array[AnyRef] = Option(hit.sortValues).getOrElse(Array.empty)
  lazy val matchedQueries: Array[String] = Option(hit.matchedQueries).getOrElse(Array.empty)
  lazy val innerHits: Map[String, SearchHits] = Option(hit.getInnerHits).map(_.asScala.toMap).getOrElse(Map.empty)
}

sealed trait RichSearchHitField {
  def name: String
  def value[T]: T
  def validate[T](prefix: String = "")(implicit reader: HitFieldReader[T]): T Or Every[ErrorMessage] = {
    reader.as(prefix)(this)
  }
}

case class SomeValueSearchHitField(name: String, _value: Any) extends RichSearchHitField {
  override def value[T]: T = _value.asInstanceOf[T]
}

case class MissingRichSearchField(name: String) extends RichSearchHitField {
  def value[T] = throw new UnsupportedOperationException
}

case class RichSearchHitFields(field: Map[String, RichSearchHitField]) {
  def apply(name: String) = field.getOrElse(name, MissingRichSearchField(name))
}
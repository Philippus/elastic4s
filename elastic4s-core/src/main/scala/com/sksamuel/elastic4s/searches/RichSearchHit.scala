package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.{Hit, HitAs, HitReader}
import org.apache.lucene.search.Explanation
import org.elasticsearch.common.bytes.BytesReference
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField
import org.elasticsearch.search.{SearchHit, SearchHits, SearchShardTarget}

import scala.collection.JavaConverters._

case class RichSearchHit(java: SearchHit) extends Hit {

  import scala.collection.mutable

  // java method aliases
  @deprecated("use .java", "2.1.2")
  def hit = java

  override def id: String = java.id
  override def index: String = java.index
  override def `type`: String = java.`type`()
  override def version: Long = java.version()

  def score: Float = java.score
  def nestedIdentity: SearchHit.NestedIdentity = java.getNestedIdentity
  def shard: SearchShardTarget = java.shard

  override def exists = true

  override def sourceAsBytes: Array[Byte] = if (java.source == null) Array.emptyByteArray else java.source
  override def sourceAsByteRef: BytesReference = java.sourceRef()
  override def sourceAsString: String = if (java.sourceAsString == null) "" else java.sourceAsString
  override def sourceAsMap: Map[String, AnyRef] = if (java.sourceAsMap == null) Map.empty else java.sourceAsMap.asScala.toMap
  override def isSourceEmpty: Boolean = !java.hasSource

  @deprecated("use sourceAsByteRef", "2.1.2")
  def sourceRef: BytesReference = java.sourceRef()

  def sourceAsMutableMap: mutable.Map[String, AnyRef] = {
    if (java.sourceAsMap == null) mutable.Map.empty else java.sourceAsMap.asScala
  }

  @deprecated("use to[T] which uses a Reader[T] typeclass", "3.0.0")
  def as[T](implicit hitas: HitAs[T], manifest: Manifest[T]): T = hitas.as(this)

  def to[T](implicit reader: HitReader[T], manifest: Manifest[T]): Either[Exception, T] = reader.read(this)

  def explanation: Option[Explanation] = Option(java.explanation)

  def fields: Map[String, RichSearchHitField] = {
    if (java.fields == null) Map.empty else java.fields.asScala.toMap.mapValues(RichSearchHitField)
  }

  def stringValue(fieldName: String): String = field(fieldName).value.toString

  override def field(fieldName: String): RichSearchHitField = fields(fieldName)
  override def fieldOpt(fieldName: String): Option[RichSearchHitField] = fields.get(fieldName)
  def fieldsSeq: Seq[RichSearchHitField] = fields.values.toSeq

  def fieldValue(fieldName: String): AnyRef = field(fieldName).value
  def fieldValueOpt(fieldName: String): AnyRef = fieldOpt(fieldName).map(_.value)

  def highlightFields: Map[String, HighlightField] = {
    if (java.highlightFields == null) Map.empty else java.highlightFields().asScala.toMap
  }

  def sortValues: Array[AnyRef] = if (java.sortValues == null) Array.empty else java.sortValues
  def matchedQueries: Array[String] = if (java.matchedQueries == null) Array.empty else java.matchedQueries
  def innerHits: Map[String, SearchHits] = {
    if (java.getInnerHits == null) Map.empty else java.getInnerHits.asScala.toMap
  }

  override def equals(other: Any): Boolean = other match {
    case hit: SearchHit => equals(RichSearchHit(hit))
    case hit: RichSearchHit =>
      this.index == hit.index && this.`type` == hit.`type` && this.sourceAsString == hit.sourceAsString
    case _ => false
  }

}

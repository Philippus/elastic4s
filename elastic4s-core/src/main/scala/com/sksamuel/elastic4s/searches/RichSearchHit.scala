package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.{Hit, HitAs}
import com.sksamuel.exts.StringOption
import org.apache.lucene.search.Explanation
import org.elasticsearch.common.bytes.BytesReference
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField
import org.elasticsearch.search.{SearchHit, SearchHits, SearchShardTarget}

import scala.collection.JavaConverters._

case class RichSearchHit(java: SearchHit) extends Hit {

  override def id: String = java.id
  override def index: String = java.index
  override def `type`: String = java.`type`()
  override def version: Long = java.version()

  def score: Float = java.score
  def nestedIdentity: SearchHit.NestedIdentity = java.getNestedIdentity
  def shard: SearchShardTarget = java.shard

  override def exists = true

  override def sourceAsBytes: Array[Byte] = Option(java.source).getOrElse(Array.empty)
  override def sourceAsByteRef: BytesReference = java.sourceRef()
  override def sourceAsString: String = StringOption(java.sourceAsString).getOrElse("")
  override def sourceAsMap: Map[String, AnyRef] = Option(java.sourceAsMap).map(_.asScala.toMap).getOrElse(Map.empty)
  override def isSourceEmpty: Boolean = !java.hasSource

  def explanation: Option[Explanation] = Option(java.explanation)

  def fields: Map[String, RichSearchHitField] =
    Option(java.fields).map(_.asScala.toMap).getOrElse(Map.empty).mapValues(RichSearchHitField)

  def stringValue(fieldName: String): String = field(fieldName).value.toString

  def field(fieldName: String): RichSearchHitField = fields(fieldName)
  def fieldOpt(fieldName: String): Option[RichSearchHitField] = fields.get(fieldName)
  def fieldsSeq: Seq[RichSearchHitField] = fields.values.toSeq

  def fieldValue(fieldName: String): AnyRef = field(fieldName).value
  def fieldValueOpt(fieldName: String): AnyRef = fieldOpt(fieldName).map(_.value)

  def highlightFields: Map[String, HighlightField] =
    Option(java.highlightFields).map(_.asScala.toMap).getOrElse(Map.empty)

  def sortValues: IndexedSeq[AnyRef] = Option(java.sortValues).map(_.toIndexedSeq).getOrElse(Array.empty[AnyRef])
  def matchedQueries: IndexedSeq[String] = Option(java.matchedQueries).map(_.toIndexedSeq).getOrElse(Array.empty[String])

  def innerHits: Map[String, SearchHits] = Option(java.getInnerHits).map(_.asScala.toMap).getOrElse(Map.empty)

  override def equals(other: Any): Boolean = other match {
    case hit: SearchHit => equals(RichSearchHit(hit))
    case hit: RichSearchHit =>
      this.index == hit.index && this.`type` == hit.`type` && this.sourceAsString == hit.sourceAsString
    case _ => false
  }

  @deprecated("use to[T] which uses a Reader[T] typeclass", "5.0.0")
  def as[T](implicit hitas: HitAs[T], manifest: Manifest[T]): T = hitas.as(this)

  @deprecated("use sourceAsByteRef", "2.1.2")
  def sourceRef: BytesReference = java.sourceRef()

  // java method aliases
  @deprecated("use .java", "2.1.2")
  def hit = java
}

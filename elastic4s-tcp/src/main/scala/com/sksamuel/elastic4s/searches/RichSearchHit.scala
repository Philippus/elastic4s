package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.Hit
import com.sksamuel.exts.StringOption
import org.apache.lucene.search.Explanation
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField
import org.elasticsearch.search.{SearchHit, SearchHits, SearchShardTarget}

import scala.collection.JavaConverters._

case class RichSearchHit(java: SearchHit) extends Hit {

  override def id: String = java.getId
  override def index: String = java.getIndex
  override def `type`: String = java.getType
  override def version: Long = java.getVersion

  override def score: Float = java.getScore
  def nestedIdentity: SearchHit.NestedIdentity = java.getNestedIdentity
  def shard: SearchShardTarget = java.getShard

  override def exists = true

  override def sourceAsString: String = StringOption(java.getSourceAsString).getOrElse("")
  override def sourceAsMap: Map[String, AnyRef] = Option(java.getSourceAsMap).map(_.asScala.toMap).getOrElse(Map.empty)

  def explanation: Option[Explanation] = Option(java.getExplanation)

  def fields: Map[String, RichSearchHitField] =
    Option(java.getFields).map(_.asScala.toMap).getOrElse(Map.empty).mapValues(RichSearchHitField)

  def stringValue(fieldName: String): String = field(fieldName).value.toString

  def field(fieldName: String): RichSearchHitField = fields(fieldName)
  def fieldOpt(fieldName: String): Option[RichSearchHitField] = fields.get(fieldName)
  def fieldsSeq: Seq[RichSearchHitField] = fields.values.toSeq

  def fieldValue(fieldName: String): AnyRef = field(fieldName).value
  def fieldValueOpt(fieldName: String): Option[AnyRef] = fieldOpt(fieldName).map(_.value)

  def highlightFields: Map[String, HighlightField] =
    Option(java.getHighlightFields).map(_.asScala.toMap).getOrElse(Map.empty)

  def sortValues: IndexedSeq[AnyRef] = Option(java.getSortValues).map(_.toIndexedSeq).getOrElse(Array.empty[AnyRef])
  def matchedQueries: IndexedSeq[String] = Option(java.getMatchedQueries).map(_.toIndexedSeq).getOrElse(Array.empty[String])

  def innerHits: Map[String, SearchHits] = Option(java.getInnerHits).map(_.asScala.toMap).getOrElse(Map.empty)

  override def equals(other: Any): Boolean = other match {
    case hit: SearchHit => equals(RichSearchHit(hit))
    case hit: RichSearchHit =>
      this.index == hit.index && this.`type` == hit.`type` && this.sourceAsString == hit.sourceAsString
    case _ => false
  }
}

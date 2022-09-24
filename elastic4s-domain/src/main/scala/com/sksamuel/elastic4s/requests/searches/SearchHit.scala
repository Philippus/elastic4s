package com.sksamuel.elastic4s.requests.searches

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.Hit
import com.sksamuel.elastic4s.requests.explain.Explanation
import com.sksamuel.elastic4s.requests.get.{HitField, MetaDataFields}
import com.sksamuel.elastic4s.requests.searches.aggs.responses.JacksonSupport

case class SearchHit(@JsonProperty("_id") id: String,
                     @JsonProperty("_index") index: String,
                     @JsonProperty("_version") version: Long,
                     @JsonProperty("_seq_no") seqNo: Long,
                     @JsonProperty("_primary_term") primaryTerm: Long,
                     @JsonProperty("_score") score: Float,
                     @JsonProperty("_parent") parent: Option[String],
                     @JsonProperty("_shard") shard: Option[String],
                     @JsonProperty("_node") node: Option[String],
                     @JsonProperty("_routing") routing: Option[String],
                     @JsonProperty("_explanation") explanation: Option[Explanation],
                     @JsonProperty("sort") sort: Option[Seq[AnyRef]],
                     private val _source: Map[String, AnyRef],
                     fields: Map[String, AnyRef],
                     @JsonProperty("highlight") private val _highlight: Option[Map[String, Seq[String]]],
                     private val inner_hits: Map[String, Map[String, Any]],
                     @JsonProperty("matched_queries") matchedQueries: Option[Set[String]])
  extends Hit {

  def highlight: Map[String, Seq[String]] = _highlight.getOrElse(Map.empty)
  def highlightFragments(name: String): Seq[String] = highlight.getOrElse(name, Nil)

  def innerHitsAsMap: Map[String, Map[String, Any]] = Option(inner_hits).getOrElse(Map.empty)

  def storedField(fieldName: String): HitField = storedFieldOpt(fieldName).get
  def storedFieldOpt(fieldName: String): Option[HitField] = fields.get(fieldName).map { v =>
    new HitField {
      override def values: Seq[AnyRef] = v match {
        case values: Seq[AnyRef] => values
        case value: AnyRef => Seq(value)
      }
      override def value: AnyRef = values.head
      override def name: String = fieldName
      override def isMetadataField: Boolean = MetaDataFields.fields.contains(name)
    }
  }

  override def sourceAsMap: Map[String, AnyRef] = _source
  override def sourceAsString: String = JacksonSupport.mapper.writeValueAsString(_source)

  override def exists: Boolean = true

  private def buildInnerHits(_hits: Map[String, Map[String, Any]]): Map[String, InnerHits] =
    Option(_hits).getOrElse(Map.empty).mapValues { hits =>
      val v = hits("hits").asInstanceOf[Map[String, AnyRef]]
      val total = v("total").asInstanceOf[Map[String, AnyRef]]
      InnerHits(
        total = Total(total("value").toString.toLong, total("relation").toString),
        maxScore = Option(v("max_score")).map(_.toString.toDouble),
        hits = v("hits").asInstanceOf[Seq[Map[String, AnyRef]]].map { hits =>
          InnerHit(
            index = hits("_index").toString,
            id = hits("_id").toString,
            nested = hits.get("_nested").map(_.asInstanceOf[Map[String, AnyRef]]).getOrElse(Map.empty),
            score = Option(hits("_score")).map(_.toString.toDouble),
            routing = hits.get("_routing").map(_.toString).getOrElse(""),
            source = hits.get("_source").map(_.asInstanceOf[Map[String, AnyRef]]).getOrElse(Map.empty),
            innerHits = buildInnerHits(hits.getOrElse("inner_hits", null).asInstanceOf[Map[String, Map[String, Any]]]),
            highlight = hits.get("highlight").map(_.asInstanceOf[Map[String, Seq[String]]]).getOrElse(Map.empty),
            sort = hits.get("sort").map(_.asInstanceOf[Seq[AnyRef]]).getOrElse(Seq.empty),
            fields = hits.get("fields").map(_.asInstanceOf[Map[String, AnyRef]]).getOrElse(Map.empty)
          )
        }
      )
    }.toMap

  def innerHits: Map[String, InnerHits] = buildInnerHits(inner_hits)
}

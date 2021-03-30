package com.sksamuel.elastic4s.requests.searches.aggs.responses.metrics

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.DocumentRef
import com.sksamuel.elastic4s.requests.searches.Total
import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AggSerde, JacksonSupport, MetricAggregation, Transformable}

case class TopHits(name: String,
                   total: Total,
                   @JsonProperty("max_score") maxScore: Option[Double],
                   hits: Seq[TopHit]) extends MetricAggregation

case class TopHit(@JsonProperty("_index") index: String,
                  @JsonProperty("_type") `type`: String,
                  @JsonProperty("_id") id: String,
                  @JsonProperty("_score") score: Option[Double],
                  sort: Seq[String],
                  @JsonProperty("_source") source: Map[String, Any]) extends Transformable {

  @deprecated("types are deprecated in elasticsearch", "7.7")
  def ref: DocumentRef = DocumentRef(index, `type`, id)
  override private[elastic4s] val data = source
}

object TopHits {

  implicit object TopHitsAggSerde extends AggSerde[TopHits] {
    override def read(name: String, data: Map[String, Any]): TopHits = apply(name, data)
  }

  def apply(name: String, data: Map[String, Any]): TopHits = {
    val hits = data("hits").asInstanceOf[Map[String, Any]]
    val result = JacksonSupport.mapper.readValue[TopHits](JacksonSupport.mapper.writeValueAsBytes(hits))
    result.copy(name = name)
  }
}

package com.sksamuel.elastic4s.requests.searches.aggs.responses.metrics

import com.sksamuel.elastic4s.JacksonSupport
import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AggSerde, MetricAggregation}

case class TopMetric(sort: List[Any], metrics: Map[String, Any])

case class TopMetrics(name: String, top: List[TopMetric]) extends MetricAggregation

object TopMetrics {

  implicit object TopHitsAggSerde extends AggSerde[TopMetrics] {
    override def read(name: String, data: Map[String, Any]): TopMetrics = apply(name, data)
  }

  def apply(name: String, data: Map[String, Any]): TopMetrics = {
    val result = JacksonSupport.mapper.readValue[TopMetrics](JacksonSupport.mapper.writeValueAsBytes(data))
    result.copy(name = name)
  }
}

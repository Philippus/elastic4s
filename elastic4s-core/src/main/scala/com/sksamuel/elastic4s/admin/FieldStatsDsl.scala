package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.fieldstats.{FieldStats, FieldStatsRequestBuilder, FieldStatsResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait FieldStatsDsl {

  implicit object FieldStatsDefinitionExecutable
    extends Executable[FieldStatsDefinition, FieldStatsResponse, FieldStatsResult] {
    override def apply(c: Client, t: FieldStatsDefinition): Future[FieldStatsResult] = {
      injectFutureAndMap(t.build(c.prepareFieldStats).execute, resp => FieldStatsResult(resp))
    }
  }
}

case class FieldStatsDefinition(indexes: Seq[String] = Nil, fields: Seq[String] = Nil, level: Option[String] = None) {

  def build(builder: FieldStatsRequestBuilder): FieldStatsRequestBuilder = {
    builder.setFields(fields: _*)
    level.foreach(builder.setLevel)
    if (indexes.nonEmpty)
      builder.setIndices(indexes: _*)
    builder
  }

  def indexes(indexes: String*): FieldStatsDefinition = {
    copy(indexes = indexes)
  }

  def level(level: String): FieldStatsDefinition = copy(level = Option(level))
}

case class FieldStatsResult(resp: FieldStatsResponse) {

  import scala.collection.JavaConverters._

  def fieldStats: Map[String, FieldStats[_]] = resp.getAllFieldStats.asScala.toMap

  def indicesMergedFieldStats: Map[String, Map[String, FieldStats[_]]] = {
    resp.getIndicesMergedFieldStats.asScala.toMap.map { case (key, value) => key -> value.asScala.toMap }
  }
}
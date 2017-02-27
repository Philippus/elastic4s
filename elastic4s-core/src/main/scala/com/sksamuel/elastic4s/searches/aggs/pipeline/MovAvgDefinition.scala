package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.movavg.MovAvgPipelineAggregationBuilder
import org.elasticsearch.search.aggregations.pipeline.movavg.models.MovAvgModelBuilder

import scala.collection.JavaConverters._

case class MovAvgDefinition(name: String,
                            bucketsPath: String,
                            format: Option[String] = None,
                            gapPolicy: Option[GapPolicy] = None,
                            minimise: Option[Boolean] = None,
                            modelBuilder: Option[MovAvgModelBuilder] = None,
                            numPredictions: Option[Integer] = None,
                            settings: Map[String, AnyRef] = Map.empty,
                            window: Option[Integer] = None,
                            metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = MovAvgPipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.movingAvg(name, bucketsPath)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    minimise.foreach(builder.minimize)
    modelBuilder.foreach(builder.modelBuilder)
    numPredictions.foreach(num => builder.predict(num))
    window.foreach(win => builder.window(win))
    builder
  }

  def minimise(minimise: Boolean): MovAvgDefinition = copy(minimise = Some(minimise))
  def modelBuilder(modelBuilder: MovAvgModelBuilder): MovAvgDefinition = copy(modelBuilder = Some(modelBuilder))
  def numPredictions(numPredictions: Integer): MovAvgDefinition = copy(numPredictions = Some(numPredictions))
  def settings(format: Map[String, AnyRef]): MovAvgDefinition = copy(settings = settings)
  def window(window: Integer): MovAvgDefinition = copy(window = Some(window))

  def format(format: String): MovAvgDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): MovAvgDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MovAvgDefinition = copy(metadata = metadata)
}

object MovAvgBuilder {
  def apply(): Unit = {

  }
}

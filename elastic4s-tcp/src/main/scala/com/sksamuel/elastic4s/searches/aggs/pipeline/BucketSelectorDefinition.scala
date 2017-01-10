package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketselector.BucketSelectorPipelineAggregationBuilder

import scala.collection.JavaConverters._

case class BucketSelectorDefinition(name: String,
                                    script: ScriptDefinition,
                                    bucketsPaths: Seq[String],
                                    gapPolicy: Option[GapPolicy] = None,
                                    metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = BucketSelectorPipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.bucketSelector(name, ScriptBuilder(script), bucketsPaths: _*)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def gapPolicy(gapPolicy: GapPolicy): BucketSelectorDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): BucketSelectorDefinition = copy(metadata = metadata)
}

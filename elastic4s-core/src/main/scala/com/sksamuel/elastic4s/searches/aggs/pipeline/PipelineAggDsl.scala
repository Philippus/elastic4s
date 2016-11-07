package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder

trait PipelineAggregationDsl {

  def avgBucketAggregation(name: String,
                           bucketsPath: String): AvgBucketDefinition = AvgBucketDefinition(name, bucketsPath)

  def bucketScriptAggregation(name: String,
                              script: ScriptDefinition,
                              bucketsPath: Seq[String]): BucketScriptDefinition =
    BucketScriptDefinition(name, script, bucketsPath)

  def cumulativeSumAggregation(name: String, bucketsPath: String): CumulativeSumDefinition =
    CumulativeSumDefinition(name, bucketsPath)

  def derivativeAggregation(name: String, bucketsPath: String): DerivativeDefinition =
    DerivativeDefinition(name, bucketsPath)

  def diffAggregation(name: String, bucketsPath: String): DiffDefinition = DiffDefinition(name, bucketsPath)

  def extendedStatsBucketAggregation(name: String, bucketsPath: String): ExtendedStatsBucketDefinition =
    ExtendedStatsBucketDefinition(name, bucketsPath)

  def maxBucketAggregation(name: String, bucketsPath: String): MaxBucketDefinition =
    MaxBucketDefinition(name, bucketsPath)

  def minBucketAggregation(name: String, bucketsPath: String): MinBucketDefinition =
    MinBucketDefinition(name, bucketsPath)

  def movingAverageAggregation(name: String, bucketsPath: String): MovAvgDefinition =
    MovAvgDefinition(name, bucketsPath)

  def percentilesBucketAggregation(name: String, bucketsPath: String): PercentilesBucketDefinition =
    PercentilesBucketDefinition(name, bucketsPath)

  def statsBucketAggregation(name: String, bucketsPath: String): StatsBucketDefinition =
    StatsBucketDefinition(name, bucketsPath)

  def sumBucketAggregation(name: String, bucketsPath: String): SumBucketDefinition =
    SumBucketDefinition(name, bucketsPath)
}

abstract class PipelineAggregationDefinition {
  type T <: PipelineAggregationBuilder
  def builder: T
  def metadata: Map[String, AnyRef]
}

package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.aggs.AbstractAggregation

trait PipelineAggregationApi {

  def avgBucketAgg(name: String, bucketsPath: String): AvgBucketDefinition = avgBucketAggregation(name, bucketsPath)
  def avgBucketAggregation(name: String,
                           bucketsPath: String): AvgBucketDefinition = AvgBucketDefinition(name, bucketsPath)

  def bucketScriptAggregation(name: String,
                              script: ScriptDefinition,
                              bucketsPath: Seq[String]): BucketScriptDefinition =
    BucketScriptDefinition(name, script, bucketsPath)

  def cumulativeSumAggregation(name: String,
                               bucketsPath: String): CumulativeSumDefinition = CumulativeSumDefinition(name, bucketsPath)

  def derivativeAggregation(name: String,
                            bucketsPath: String): DerivativeDefinition = DerivativeDefinition(name, bucketsPath)

  def diffAggregation(name: String,
                      bucketsPath: String): DiffDefinition = DiffDefinition(name, bucketsPath)

  def extendedStatsBucketAggregation(name: String,
                                     bucketsPath: String): ExtendedStatsBucketDefinition =
    ExtendedStatsBucketDefinition(name, bucketsPath)

  def maxBucketAgg(name: String, bucketsPath: String): MaxBucketDefinition = maxBucketAggregation(name, bucketsPath)
  def maxBucketAggregation(name: String,
                           bucketsPath: String): MaxBucketDefinition = MaxBucketDefinition(name, bucketsPath)

  def minBucketAggregation(name: String,
                           bucketsPath: String): MinBucketDefinition = MinBucketDefinition(name, bucketsPath)

  def movingAverageAggregation(name: String,
                               bucketsPath: String): MovAvgDefinition = MovAvgDefinition(name, bucketsPath)

  def percentilesBucketAggregation(name: String,
                                   bucketsPath: String): PercentilesBucketDefinition = PercentilesBucketDefinition(name, bucketsPath)

  def statsBucketAggregation(name: String,
                             bucketsPath: String): StatsBucketDefinition = StatsBucketDefinition(name, bucketsPath)

  def sumBucketAggregation(name: String,
                           bucketsPath: String): SumBucketDefinition = SumBucketDefinition(name, bucketsPath)
}

trait PipelineAggregationDefinition extends AbstractAggregation

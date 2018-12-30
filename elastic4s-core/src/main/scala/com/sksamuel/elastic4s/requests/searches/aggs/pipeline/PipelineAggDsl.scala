package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.aggs.AbstractAggregation
import com.sksamuel.elastic4s.requests.searches.sort.Sort

trait PipelineAggregationApi {

  def avgBucketAgg(name: String, bucketsPath: String): AvgBucketPipelineAgg = avgBucketAggregation(name, bucketsPath)
  def avgBucketAggregation(name: String, bucketsPath: String): AvgBucketPipelineAgg =
    AvgBucketPipelineAgg(name, bucketsPath)

  def bucketSelectorAggregation(name: String,
                                script: Script,
                                bucketsPathMap: Map[String, String]): BucketSelectorPipelineAgg =
    BucketSelectorPipelineAgg(name, script, bucketsPathMap)

  def bucketSortAggregation(name: String, sort: Seq[Sort]): BucketSortPipelineAgg =
    BucketSortPipelineAgg(name, sort)

  def bucketScriptAggregation(name: String, script: Script, bucketsPath: Map[String, String]): BucketScriptPipelineAgg =
    BucketScriptPipelineAgg(name, script, bucketsPath)

  def cumulativeSumAggregation(name: String, bucketsPath: String): CumulativeSumPipelineAgg =
    CumulativeSumPipelineAgg(name, bucketsPath)

  def derivativeAggregation(name: String, bucketsPath: String): DerivativePipelineAgg =
    DerivativePipelineAgg(name, bucketsPath)

  def diffAggregation(name: String, bucketsPath: String): DiffPipelineAgg = DiffPipelineAgg(name, bucketsPath)

  def extendedStatsBucketAggregation(name: String, bucketsPath: String): ExtendedStatsBucketPipelineAgg =
    ExtendedStatsBucketPipelineAgg(name, bucketsPath)

  def maxBucketAgg(name: String, bucketsPath: String): MaxBucket = maxBucketAggregation(name, bucketsPath)
  def maxBucketAggregation(name: String, bucketsPath: String): MaxBucket =
    MaxBucket(name, bucketsPath)

  def minBucketAggregation(name: String, bucketsPath: String): MinBucketPipelineAgg =
    MinBucketPipelineAgg(name, bucketsPath)

  def movingAverageAggregation(name: String, bucketsPath: String): MovAvgPipelineAgg =
    MovAvgPipelineAgg(name, bucketsPath)

  def percentilesBucketAggregation(name: String, bucketsPath: String): PercentilesBucketPipelineAgg =
    PercentilesBucketPipelineAgg(name, bucketsPath)

  def statsBucketAggregation(name: String, bucketsPath: String): StatsBucketPipelineAgg =
    StatsBucketPipelineAgg(name, bucketsPath)

  def sumBucketAggregation(name: String, bucketsPath: String): SumBucketPipelineAgg =
    SumBucketPipelineAgg(name, bucketsPath)
}

trait PipelineAgg extends AbstractAggregation

package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.PipelineAggregationBuilder

object PipelineAggregationBuilderFn {
  def apply(pipeline: PipelineAggregationDefinition): PipelineAggregationBuilder = pipeline match {
    case p: AvgBucketDefinition => AvgBucketPipelineBuilder(p)
    case p: CumulativeSumDefinition => CumulativeSumPipelineBuilder(p)
    case p: DerivativeDefinition => DerivativePipelineBuilder(p)
    case p: DiffDefinition => SerialDiffPipelineBuilder(p)
    case p: ExtendedStatsBucketDefinition => ExtendedStatsBucketBuilder(p)
    case p: MaxBucketDefinition => MaxBucketPipelineBuilder(p)
    case p: MinBucketDefinition => MinBucketPipelineBuilder(p)
    case p: MovAvgDefinition => MovAvgPipelineBuilder(p)
    case p: PercentilesBucketDefinition => PercentilesBucketPipelineBuilder(p)
    case p: StatsBucketDefinition => StatsBucketPipelineBuilder(p)
    case p: SumBucketDefinition => SumBucketPipelineBuilder(p)
  }
}

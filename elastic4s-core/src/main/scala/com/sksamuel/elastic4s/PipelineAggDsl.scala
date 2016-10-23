package com.sksamuel.elastic4s

import org.elasticsearch.script.Script
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval
import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.avg.AvgBucketBuilder
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.max.MaxBucketBuilder
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.min.MinBucketBuilder
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.percentile.PercentilesBucketBuilder
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.StatsBucketBuilder
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.extended.ExtendedStatsBucketBuilder
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.sum.SumBucketBuilder
import org.elasticsearch.search.aggregations.pipeline.bucketscript.BucketScriptBuilder
import org.elasticsearch.search.aggregations.pipeline.cumulativesum.CumulativeSumBuilder
import org.elasticsearch.search.aggregations.pipeline.derivative.DerivativeBuilder
import org.elasticsearch.search.aggregations.pipeline.having.BucketSelectorBuilder
import org.elasticsearch.search.aggregations.pipeline.movavg.MovAvgBuilder
import org.elasticsearch.search.aggregations.pipeline.movavg.models.MovAvgModelBuilder
import org.elasticsearch.search.aggregations.pipeline.serialdiff.SerialDiffBuilder
import org.elasticsearch.search.aggregations.pipeline.{PipelineAggregatorBuilder, PipelineAggregatorBuilders}


trait PipelineAggregationDsl {
  def avgBucketAggregation(name: String): AvgBucketDefinition = AvgBucketDefinition(name)
  def bucketScriptAggregation(name: String): BucketScriptDefinition = BucketScriptDefinition(name)
  def cumulativeSumAggregation(name: String): CumulativeSumDefinition = CumulativeSumDefinition(name)
  def derivativeAggregation(name: String): DerivativeDefinition = DerivativeDefinition(name)
  def diffAggregation(name: String): DiffDefinition = DiffDefinition(name)
  def extendedStatsBucketAggregation(name: String): ExtendedStatsBucketDefinition = ExtendedStatsBucketDefinition(name)
  def maxBucketAggregation(name: String): MaxBucketDefinition = MaxBucketDefinition(name)
  def minBucketAggregation(name: String): MinBucketDefinition = MinBucketDefinition(name)
  def movingAverageAggregation(name: String): MovAvgDefinition = MovAvgDefinition(name)
  def percentilesBucketAggregation(name: String): PercentilesBucketDefinition = PercentilesBucketDefinition(name)
  def statsBucketAggregation(name: String): StatsBucketDefinition = StatsBucketDefinition(name)
  def sumBucketAggregation(name: String): SumBucketDefinition = SumBucketDefinition(name)
}


abstract class PipelineDefinition extends AbstractAggregationDefinition {

  type T <: PipelineAggregatorBuilder[T]

  def metadata: Map[String, AnyRef]
  def bucketPaths: Seq[String]

  def build(builder: T): T = {
    import scala.collection.JavaConverters._
    if (bucketPaths.nonEmpty)
      builder.setBucketsPaths(bucketPaths: _*)
    if (metadata.nonEmpty)
      builder.setMetaData(metadata.asJava)
    builder
  }
}


case class AvgBucketDefinition(name: String,
                               gapPolicy: Option[GapPolicy] = None,
                               format: Option[String] = None,
                               metadata: Map[String, AnyRef] = Map.empty,
                               bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = AvgBucketBuilder

  def builder: T = {
    val builder = super.build(PipelineAggregatorBuilders.avgBucket(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): AvgBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): AvgBucketDefinition = copy(gapPolicy = Some(gapPolicy))

  def metadata(metadata: Map[String, AnyRef]): AvgBucketDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): AvgBucketDefinition = copy(bucketPaths = bucketPaths)
}


case class BucketScriptDefinition(name: String,
                                  format: Option[String] = None,
                                  gapPolicy: Option[GapPolicy] = None,
                                  script: Option[Script] = None,
                                  metadata: Map[String, AnyRef] = Map.empty,
                                  bucketPaths: Seq[String] = Nil,
                                  bucketPathsMap: Map[String, String] = Map.empty) extends PipelineDefinition {
  type T = BucketScriptBuilder

  def builder: T = {
    import scala.collection.JavaConverters._
    val builder = super.build(PipelineAggregatorBuilders.bucketScript(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    script.foreach(builder.script)
    if (bucketPathsMap.nonEmpty)
      builder.setBucketsPathsMap(bucketPathsMap.asJava)
    builder
  }

  def script(script: Script): BucketScriptDefinition = copy(script = Some(script))
  def format(format: String): BucketScriptDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): BucketScriptDefinition = copy(gapPolicy = Some(gapPolicy))
  def bucketPathsMap(map: Map[String, String]): BucketScriptDefinition = copy(bucketPathsMap = map)

  def metadata(metadata: Map[String, AnyRef]): BucketScriptDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): BucketScriptDefinition = copy(bucketPaths = bucketPaths)
}


case class CumulativeSumDefinition(name: String,
                                   format: Option[String] = None,
                                   metadata: Map[String, AnyRef] = Map.empty,
                                   bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = CumulativeSumBuilder

  def builder: T = {
    val builder = super.build(PipelineAggregatorBuilders.cumulativeSum(name))
    format.foreach(builder.format)
    builder
  }

  def format(format: String): CumulativeSumDefinition = copy(format = Some(format))
  def metadata(metadata: Map[String, AnyRef]): CumulativeSumDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): CumulativeSumDefinition = copy(bucketPaths = bucketPaths)
}


case class DerivativeDefinition(name: String,
                                format: Option[String] = None,
                                gapPolicy: Option[GapPolicy] = None,
                                unit: Option[DateHistogramInterval] = None,
                                unitString: Option[String] = None,
                                metadata: Map[String, AnyRef] = Map.empty,
                                bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = DerivativeBuilder

  def builder: T = {
    val builder = super.build(PipelineAggregatorBuilders.derivative(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    unit.foreach(builder.unit)
    unitString.foreach(builder.unit)
    builder
  }

  def unit(unit: DateHistogramInterval): DerivativeDefinition = copy(unit = Some(unit))
  def unit(unit: String): DerivativeDefinition = copy(unitString = Some(unit))

  def format(format: String): DerivativeDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): DerivativeDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): DerivativeDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): DerivativeDefinition = copy(bucketPaths = bucketPaths)
}


case class DiffDefinition(name: String,
                          format: Option[String] = None,
                          gapPolicy: Option[GapPolicy] = None,
                          lag: Option[Integer] = None,
                          metadata: Map[String, AnyRef] = Map.empty,
                          bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = SerialDiffBuilder

  def builder: T = {
    val builder = super.build(PipelineAggregatorBuilders.diff(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    lag.foreach(builder.lag)
    builder
  }

  def format(format: String): DiffDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): DiffDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): DiffDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): DiffDefinition = copy(bucketPaths = bucketPaths)
}


case class ExtendedStatsBucketDefinition(name: String,
                                         format: Option[String] = None,
                                         gapPolicy: Option[GapPolicy] = None,
                                         metadata: Map[String, AnyRef] = Map.empty,
                                         bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = ExtendedStatsBucketBuilder

  def builder: T = {
    val builder = super.build(PipelineAggregatorBuilders.extendedStatsBucket(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): ExtendedStatsBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): ExtendedStatsBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): ExtendedStatsBucketDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): ExtendedStatsBucketDefinition = copy(bucketPaths = bucketPaths)
}


case class BucketSelectorDefinition(name: String,
                                    gapPolicy: Option[GapPolicy] = None,
                                    script: Option[Script] = None,
                                    metadata: Map[String, AnyRef] = Map.empty,
                                    bucketPaths: Seq[String] = Nil,
                                    bucketPathsMap: Map[String, String] = Map.empty) extends PipelineDefinition {
  type T = BucketSelectorBuilder

  def builder: T = {
    import scala.collection.JavaConverters._
    val builder = super.build(PipelineAggregatorBuilders.having(name))
    script.foreach(builder.script)
    if (bucketPathsMap.nonEmpty)
      builder.setBucketsPathsMap(bucketPathsMap.asJava)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def script(script: Script): BucketSelectorDefinition = copy(script = Some(script))
  def gapPolicy(gapPolicy: GapPolicy): BucketSelectorDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): BucketSelectorDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): BucketSelectorDefinition = copy(bucketPaths = bucketPaths)
  def bucketPathsMap(map: Map[String, String]): BucketSelectorDefinition = copy(bucketPathsMap = map)
}


case class MaxBucketDefinition(name: String,
                               format: Option[String] = None,
                               gapPolicy: Option[GapPolicy] = None,
                               metadata: Map[String, AnyRef] = Map.empty,
                               bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = MaxBucketBuilder

  def builder: T = {
    val builder = super.build(PipelineAggregatorBuilders.maxBucket(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): MaxBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): MaxBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MaxBucketDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): MaxBucketDefinition = copy(bucketPaths = bucketPaths)
}


case class MinBucketDefinition(name: String,
                               format: Option[String] = None,
                               gapPolicy: Option[GapPolicy] = None,
                               metadata: Map[String, AnyRef] = Map.empty,
                               bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = MinBucketBuilder

  def builder: T = {
    val builder = super.build(PipelineAggregatorBuilders.minBucket(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): MinBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): MinBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MinBucketDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): MinBucketDefinition = copy(bucketPaths = bucketPaths)
}


case class MovAvgDefinition(name: String,
                            format: Option[String] = None,
                            gapPolicy: Option[GapPolicy] = None,
                            minimise: Option[Boolean] = None,
                            modelBuilder: Option[MovAvgModelBuilder] = None,
                            numPredictions: Option[Integer] = None,
                            settings: Map[String, AnyRef] = Map.empty,
                            window: Option[Integer] = None,
                            metadata: Map[String, AnyRef] = Map.empty,
                            bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = MovAvgBuilder

  def builder: T = {
    import scala.collection.JavaConverters._
    val builder = super.build(PipelineAggregatorBuilders.movingAvg(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    minimise.foreach(builder.minimize)
    modelBuilder.foreach(builder.modelBuilder)
    numPredictions.foreach(num => builder.predict(num))
    if (settings.nonEmpty)
      builder.settings(settings.asJava)
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
  def bucketPaths(bucketPaths: Seq[String]): MovAvgDefinition = copy(bucketPaths = bucketPaths)
}


case class PercentilesBucketDefinition(name: String,
                                       format: Option[String] = None,
                                       gapPolicy: Option[GapPolicy] = None,
                                       percents: Seq[Double] = Nil,
                                       metadata: Map[String, AnyRef] = Map.empty,
                                       bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = PercentilesBucketBuilder

  def builder: T = {
    val builder = super.build(PipelineAggregatorBuilders.percentilesBucket(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    if (percents.nonEmpty)
      builder.percents(percents.map(d => d: java.lang.Double).toArray)
    builder
  }

  def format(format: String): PercentilesBucketDefinition = copy(format = Some(format))
  def percents(first: Double, rest: Double*): PercentilesBucketDefinition = percents(first +: rest)
  def percents(percents: Seq[Double]): PercentilesBucketDefinition = copy(percents = percents)
  def gapPolicy(gapPolicy: GapPolicy): PercentilesBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): PercentilesBucketDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): PercentilesBucketDefinition = copy(bucketPaths = bucketPaths)
}


case class StatsBucketDefinition(name: String,
                                 format: Option[String] = None,
                                 gapPolicy: Option[GapPolicy] = None,
                                 metadata: Map[String, AnyRef] = Map.empty,
                                 bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = StatsBucketBuilder

  def builder: T = {
    val builder = super.build(PipelineAggregatorBuilders.statsBucket(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): StatsBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): StatsBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): StatsBucketDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): StatsBucketDefinition = copy(bucketPaths = bucketPaths)
}


case class SumBucketDefinition(name: String,
                               format: Option[String] = None,
                               gapPolicy: Option[GapPolicy] = None,
                               metadata: Map[String, AnyRef] = Map.empty,
                               bucketPaths: Seq[String] = Nil) extends PipelineDefinition {
  type T = SumBucketBuilder

  def builder: T = {
    val builder = super.build(PipelineAggregatorBuilders.sumBucket(name))
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): SumBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): SumBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): SumBucketDefinition = copy(metadata = metadata)
  def bucketPaths(bucketPaths: Seq[String]): SumBucketDefinition = copy(bucketPaths = bucketPaths)
}

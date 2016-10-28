package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.{Executable, Indexes}
import org.elasticsearch.action.ShardOperationFailedException
import org.elasticsearch.action.admin.indices.cache.clear.{ClearIndicesCacheRequestBuilder, ClearIndicesCacheResponse}
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse
import org.elasticsearch.action.admin.indices.stats.{ShardStats, IndexStats, CommonStats, IndicesStatsResponse}
import org.elasticsearch.action.support.IndicesOptions
import org.elasticsearch.client.Client
import org.elasticsearch.cluster.routing.ShardRouting
import org.elasticsearch.index.engine.Segment
import org.elasticsearch.index.shard.ShardId

import scala.concurrent.Future

trait IndexAdminDsl {

  def refreshIndex(indexes: Iterable[String]): RefreshIndexDefinition = RefreshIndexDefinition(indexes.toSeq)
  def refreshIndex(indexes: String*): RefreshIndexDefinition = RefreshIndexDefinition(indexes)

  def indexStats(indexes: Indexes): IndicesStatsDefinition = IndicesStatsDefinition(indexes)
  def indexStats(first: String, rest: String*): IndicesStatsDefinition = indexStats(first +: rest)

  def typesExist(types: String*): TypesExistExpectsIn = typesExist(types)
  def typesExist(types: Iterable[String]): TypesExistExpectsIn = new TypesExistExpectsIn(types)
  class TypesExistExpectsIn(types: Iterable[String]) {
    def in(indexes: String*): TypesExistsDefinition = TypesExistsDefinition(indexes, types.toSeq)
  }

  def closeIndex(index: String): CloseIndexDefinition = CloseIndexDefinition(index)
  def openIndex(index: String): OpenIndexDefinition = OpenIndexDefinition(index)

  implicit object OpenIndexDefinitionExecutable
    extends Executable[OpenIndexDefinition, OpenIndexResponse, OpenIndexResponse] {
    override def apply(c: Client, t: OpenIndexDefinition): Future[OpenIndexResponse] = {
      injectFuture(c.admin.indices.prepareOpen(t.index).execute)
    }
  }

  implicit object CloseIndexDefinitionExecutable
    extends Executable[CloseIndexDefinition, CloseIndexResponse, CloseIndexResponse] {
    override def apply(c: Client, t: CloseIndexDefinition): Future[CloseIndexResponse] = {
      injectFuture(c.admin.indices.prepareClose(t.index).execute)
    }
  }

  implicit object GetSegmentsDefinitionExecutable
    extends Executable[GetSegmentsDefinition, IndicesSegmentResponse, GetSegmentsResult] {
    override def apply(c: Client, t: GetSegmentsDefinition): Future[GetSegmentsResult] = {
      injectFutureAndMap(c.admin.indices.prepareSegments(t.indexes.values: _*).execute)(GetSegmentsResult.apply)
    }
  }

  implicit object IndexExistsDefinitionExecutable
    extends Executable[IndexExistsDefinition, IndicesExistsResponse, IndicesExistsResponse] {
    override def apply(c: Client, t: IndexExistsDefinition): Future[IndicesExistsResponse] = {
      injectFuture(c.admin.indices.prepareExists(t.indexes: _*).execute)
    }
  }

  implicit object TypesExistsDefinitionExecutable
    extends Executable[TypesExistsDefinition, TypesExistsResponse, TypesExistsResponse] {
    override def apply(c: Client, t: TypesExistsDefinition): Future[TypesExistsResponse] = {
      injectFuture(c.admin.indices.prepareTypesExists(t.indexes: _*).setTypes(t.types: _*).execute)
    }
  }

  implicit object IndicesStatsDefinitionExecutable
    extends Executable[IndicesStatsDefinition, IndicesStatsResponse, IndicesStatsResult] {
    override def apply(c: Client, t: IndicesStatsDefinition): Future[IndicesStatsResult] = {
      injectFutureAndMap(c.admin.indices.prepareStats(t.indexes.values: _*).execute)(IndicesStatsResult.apply)
    }
  }

  implicit object ClearIndicesCacheResponseExecutable
    extends Executable[ClearCacheDefinition, ClearIndicesCacheResponse, ClearIndicesCacheResponse] {
    override def apply(c: Client, t: ClearCacheDefinition): Future[ClearIndicesCacheResponse] = {
      injectFuture(t.build(c.admin.indices.prepareClearCache(t.indexes: _*)).execute)
    }
  }

  implicit object FlushIndexDefinitionExecutable
    extends Executable[FlushIndexDefinition, FlushResponse, FlushResponse] {
    override def apply(c: Client, t: FlushIndexDefinition): Future[FlushResponse] = {
      injectFuture(c.admin.indices.prepareFlush(t.indexes: _*).execute)
    }
  }

  implicit object RefreshDefinitionExecutable
    extends Executable[RefreshIndexDefinition, RefreshResponse, RefreshResponse] {
    override def apply(c: Client, t: RefreshIndexDefinition): Future[RefreshResponse] = {
      injectFuture(c.admin.indices.prepareRefresh(t.indexes: _*).execute)
    }
  }
}

case class OpenIndexDefinition(index: String)
case class CloseIndexDefinition(index: String)
case class GetSegmentsDefinition(indexes: Indexes)
case class IndexExistsDefinition(indexes: Seq[String])
case class TypesExistsDefinition(indexes: Seq[String], types: Seq[String])
case class IndicesStatsDefinition(indexes: Indexes)

case class ClearCacheDefinition(indexes: Seq[String],
                                fieldDataCache: Option[Boolean] = None,
                                requestCache: Option[Boolean] = None,
                                indicesOptions: Option[IndicesOptions] = None,
                                queryCache: Option[Boolean] = None,
                                indices: Seq[String] = Nil,
                                fields: Seq[String] = Nil) {

  def build(builder: ClearIndicesCacheRequestBuilder): ClearIndicesCacheRequestBuilder = {
    fieldDataCache.foreach(builder.setFieldDataCache)
    if (fields.nonEmpty)
      builder.setFields(fields: _*)
    if (indices.nonEmpty)
      builder.setFields(indices: _*)
    requestCache.foreach(builder.setRequestCache)
    queryCache.foreach(builder.setQueryCache)
    indicesOptions.foreach(builder.setIndicesOptions)
    builder
  }
}

case class FlushIndexDefinition(indexes: Seq[String])
case class RefreshIndexDefinition(indexes: Seq[String])

case class IndicesStatsResult(original: IndicesStatsResponse) {

  import scala.collection.JavaConverters._

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getPrimaries() = original.getPrimaries

  @deprecated("Use the scala idiomatic methods", "2.0")
  def asMap() = original.asMap

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getIndices() = original.getIndices

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getTotal(): CommonStats = original.getTotal

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getShards(): Array[org.elasticsearch.action.admin.indices.stats.ShardStats] = original.getShards

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getIndex(name: String) = original.getIndex(name)

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getAt(pos: Int) = original.getAt(pos)

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getTotalShards() = original.getTotalShards

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getFailedShards() = original.getFailedShards

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getSuccessfulShards() = original.getSuccessfulShards

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getShardFailures() = original.getShardFailures

  def primaries: CommonStats = original.getPrimaries
  def routing: Map[ShardRouting, ShardStats] = original.asMap.asScala.toMap
  def indexStats: Map[String, IndexStats] = original.getIndices.asScala.toMap
  def totalStats: CommonStats = original.getTotal
  def shardStats: Seq[org.elasticsearch.action.admin.indices.stats.ShardStats] = original.getShards.toSeq
  def indexNames: Set[String] = indexStats.keySet
}

case class GetSegmentsResult(original: IndicesSegmentResponse) {

  import scala.collection.JavaConverters._

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getTotalShards() = original.getTotalShards

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getFailedShards() = original.getFailedShards

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getSuccessfulShards() = original.getSuccessfulShards

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getShardFailures() = original.getShardFailures

  @deprecated("Use the scala idiomatic methods", "2.0")
  def getIndices() = original.getIndices

  def totalShards: Integer = original.getTotalShards
  def failedShards: Integer = original.getFailedShards
  def successfulShards: Integer = original.getSuccessfulShards
  def shardFailures: Seq[ShardOperationFailedException] = Option(original.getShardFailures).map(_.toSeq).getOrElse(Nil)

  def indices: Map[String, IndexSegments] = {
    Option(original.getIndices).map(_.asScala.map { case (k, v) => k -> IndexSegments(v) }.toMap).getOrElse(Map.empty)
  }
}

case class IndexSegments(original: org.elasticsearch.action.admin.indices.segments.IndexSegments) {

  import scala.collection.JavaConverters._

  def index: String = original.getIndex

  def shards: Map[Integer, IndexShardSegments] = {
    Option(original.getShards).map(_.asScala.map { case (k, v) => k -> IndexShardSegments(v) }.toMap)
      .getOrElse(Map.empty)
  }
}

case class IndexShardSegments(original: org.elasticsearch.action.admin.indices.segments.IndexShardSegments) {
  def shards: Seq[ShardSegments] = Option(original.getShards).map(_.toSeq.map(ShardSegments.apply)).getOrElse(Nil)
  def shardId: ShardId = original.getShardId
}

case class ShardSegments(original: org.elasticsearch.action.admin.indices.segments.ShardSegments) {

  import scala.collection.JavaConverters._

  def numberOfCommitted: Integer = original.getNumberOfCommitted
  def numberOfSearch: Integer = original.getNumberOfSearch
  def segments: Seq[Segment] = Option(original.getSegments).map(_.asScala).getOrElse(Nil)
  def shardRouting = original.getShardRouting
}


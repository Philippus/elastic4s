package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s._
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheResponse
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.rollover.RolloverResponse
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait IndexAdminExecutables {

  implicit object OpenIndexDefinitionExecutable extends Executable[OpenIndex, OpenIndexResponse, OpenIndexResponse] {
    override def apply(c: Client, t: OpenIndex): Future[OpenIndexResponse] =
      injectFuture(c.admin.indices.prepareOpen(t.indexes.values: _*).execute(_))
  }

  implicit object CloseIndexDefinitionExecutable
      extends Executable[CloseIndex, CloseIndexResponse, CloseIndexResponse] {
    override def apply(c: Client, t: CloseIndex): Future[CloseIndexResponse] =
      injectFuture(c.admin.indices.prepareClose(t.indexes.values: _*).execute(_))
  }

  implicit object GetSegmentsDefinitionExecutable
      extends Executable[GetSegments, IndicesSegmentResponse, GetSegmentsResult] {
    override def apply(c: Client, t: GetSegments): Future[GetSegmentsResult] =
      injectFutureAndMap(c.admin.indices.prepareSegments(t.indexes.values: _*).execute)(GetSegmentsResult.apply)
  }

  implicit object IndexExistsDefinitionExecutable
      extends Executable[IndicesExists, IndicesExistsResponse, IndicesExistsResponse] {
    override def apply(c: Client, t: IndicesExists): Future[IndicesExistsResponse] =
      injectFuture(c.admin.indices.prepareExists(t.indexes.values: _*).execute(_))
  }

  implicit object RolloverDefinitionExecutable extends Executable[RolloverIndex, RolloverResponse, RolloverResponse] {
    override def apply(c: Client, r: RolloverIndex): Future[RolloverResponse] = {
      val req = RolloverBuilderFn(c, r)
      injectFuture(req.execute(_))
    }
  }

  implicit object TypesExistsDefinitionExecutable
      extends Executable[TypesExists, TypesExistsResponse, TypesExistsResponse] {
    override def apply(c: Client, t: TypesExists): Future[TypesExistsResponse] =
      injectFuture(c.admin.indices.prepareTypesExists(t.indexes: _*).setTypes(t.types: _*).execute(_))
  }

  implicit object IndicesStatsDefinitionExecutable
      extends Executable[IndexStats, IndicesStatsResponse, IndicesStatsResult] {
    override def apply(c: Client, t: IndexStats): Future[IndicesStatsResult] =
      injectFutureAndMap(c.admin.indices.prepareStats(t.indices.values: _*).execute)(IndicesStatsResult.apply)
  }

  implicit object ClearIndicesCacheExecutable
      extends Executable[ClearCache, ClearIndicesCacheResponse, ClearIndicesCacheResponse] {

    override def apply(c: Client, req: ClearCache): Future[ClearIndicesCacheResponse] = {

      val builder = c.admin().indices().prepareClearCache(req.indexes: _*)
      req.fieldDataCache.foreach(builder.setFieldDataCache)
      if (req.fields.nonEmpty)
        builder.setFields(req.fields: _*)
      req.requestCache.foreach(builder.setRequestCache)
      req.queryCache.foreach(builder.setQueryCache)
      req.indicesOptions.map(EnumConversions.indicesopts).foreach(builder.setIndicesOptions)

      injectFuture(builder.execute(_))
    }
  }

  implicit object FlushIndexDefinitionExecutable extends Executable[FlushIndex, FlushResponse, FlushResponse] {
    override def apply(c: Client, t: FlushIndex): Future[FlushResponse] =
      injectFuture(c.admin.indices.prepareFlush(t.indexes: _*).execute(_))
  }

  implicit object RefreshDefinitionExecutable extends Executable[RefreshIndex, RefreshResponse, RefreshResponse] {
    override def apply(c: Client, t: RefreshIndex): Future[RefreshResponse] =
      injectFuture(c.admin.indices.prepareRefresh(t.indexes: _*).execute(_))
  }
}

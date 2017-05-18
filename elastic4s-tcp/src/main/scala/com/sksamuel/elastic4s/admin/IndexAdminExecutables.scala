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
import org.elasticsearch.action.admin.indices.shrink.ShrinkResponse
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait IndexAdminExecutables {

  implicit object ShrinkDefinitionExecutable
    extends Executable[ShrinkDefinition, ShrinkResponse, ShrinkResponse] {
    override def apply(c: Client, t: ShrinkDefinition): Future[ShrinkResponse] = {
      val builder = ShrinkBuilderFn(c, t)
      injectFuture(builder.execute(_))
    }
  }

  implicit object OpenIndexDefinitionExecutable
    extends Executable[OpenIndexDefinition, OpenIndexResponse, OpenIndexResponse] {
    override def apply(c: Client, t: OpenIndexDefinition): Future[OpenIndexResponse] = {
      injectFuture(c.admin.indices.prepareOpen(t.indexes.values: _*).execute(_))
    }
  }

  implicit object CloseIndexDefinitionExecutable
    extends Executable[CloseIndexDefinition, CloseIndexResponse, CloseIndexResponse] {
    override def apply(c: Client, t: CloseIndexDefinition): Future[CloseIndexResponse] = {
      injectFuture(c.admin.indices.prepareClose(t.indexes.values: _*).execute(_))
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
      injectFuture(c.admin.indices.prepareExists(t.index).execute(_))
    }
  }

  implicit object RolloverDefinitionExecutable
    extends Executable[RolloverDefinition, RolloverResponse, RolloverResponse] {
    override def apply(c: Client, r: RolloverDefinition): Future[RolloverResponse] = {
      val req = RolloverBuilderFn(c, r)
      injectFuture(req.execute(_))
    }
  }

  implicit object TypesExistsDefinitionExecutable
    extends Executable[TypesExistsDefinition, TypesExistsResponse, TypesExistsResponse] {
    override def apply(c: Client, t: TypesExistsDefinition): Future[TypesExistsResponse] = {
      injectFuture(c.admin.indices.prepareTypesExists(t.indexes: _*).setTypes(t.types: _*).execute(_))
    }
  }

  implicit object IndicesStatsDefinitionExecutable
    extends Executable[IndicesStatsDefinition, IndicesStatsResponse, IndicesStatsResult] {
    override def apply(c: Client, t: IndicesStatsDefinition): Future[IndicesStatsResult] = {
      injectFutureAndMap(c.admin.indices.prepareStats(t.indexes.values: _*).execute)(IndicesStatsResult.apply)
    }
  }

  implicit object ClearIndicesCacheExecutable
    extends Executable[ClearCacheDefinition, ClearIndicesCacheResponse, ClearIndicesCacheResponse] {

    override def apply(c: Client, req: ClearCacheDefinition): Future[ClearIndicesCacheResponse] = {

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

  implicit object FlushIndexDefinitionExecutable
    extends Executable[FlushIndexDefinition, FlushResponse, FlushResponse] {
    override def apply(c: Client, t: FlushIndexDefinition): Future[FlushResponse] = {
      injectFuture(c.admin.indices.prepareFlush(t.indexes: _*).execute(_))
    }
  }

  implicit object RefreshDefinitionExecutable
    extends Executable[RefreshIndexDefinition, RefreshResponse, RefreshResponse] {
    override def apply(c: Client, t: RefreshIndexDefinition): Future[RefreshResponse] = {
      injectFuture(c.admin.indices.prepareRefresh(t.indexes: _*).execute(_))
    }
  }
}

package com.sksamuel.elastic4s.testkit

import java.util
import java.util.Locale

import com.sksamuel.exts.OptionImplicits._
import com.sksamuel.elastic4s.bulk.RichBulkResponse
import com.sksamuel.elastic4s.get.{RichGetResponse, RichMultiGetResponse}
import com.sksamuel.elastic4s.http.Shards
import com.sksamuel.elastic4s.http.bulk.{BulkResponse, BulkResponseItem, BulkResponseItems}
import com.sksamuel.elastic4s.http.cluster.ClusterHealthResponse
import com.sksamuel.elastic4s.http.delete.{DeleteByQueryResponse, DeleteResponse}
import com.sksamuel.elastic4s.http.explain.ExplainResponse
import com.sksamuel.elastic4s.http.get.{GetResponse, MultiGetResponse}
import com.sksamuel.elastic4s.http.index._
import com.sksamuel.elastic4s.http.index.admin._
import com.sksamuel.elastic4s.http.index.mappings.PutMappingResponse
import com.sksamuel.elastic4s.http.search.{ClearScrollResponse, SearchHit, SearchHits}
import com.sksamuel.elastic4s.http.update.UpdateResponse
import com.sksamuel.elastic4s.http.validate.ValidateResponse
import com.sksamuel.elastic4s.index.RichIndexResponse
import com.sksamuel.elastic4s.searches.{ClearScrollResult, RichSearchResponse}
import com.sksamuel.elastic4s.update.RichUpdateResponse
import org.elasticsearch.action.DocWriteResponse
import org.elasticsearch.action.admin.cluster.health.{ClusterHealthResponse => TcpClusterHealthResponse}
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheResponse
import org.elasticsearch.action.admin.indices.close.{CloseIndexResponse => TcpCloseIndexResponse}
import org.elasticsearch.action.admin.indices.create.{CreateIndexResponse => TcpCreateIndexResponse}
import org.elasticsearch.action.admin.indices.delete.{DeleteIndexResponse => TcpDeleteIndexResponse}
import org.elasticsearch.action.admin.indices.mapping.put.{PutMappingResponse => TcpPutMappingResponse}
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.admin.indices.open.{OpenIndexResponse => TcpOpenIndexResponse}
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse
import org.elasticsearch.action.delete.{DeleteResponse => TcpDeleteResponse}
import org.elasticsearch.action.explain.{ExplainResponse => TcpExplainResponse}
import org.elasticsearch.action.bulk.byscroll.{BulkByScrollResponse, BulkByScrollTask}

import scala.collection.JavaConverters._

trait ResponseConverter[T, R] {
  def convert(response: T): R
}

object ResponseConverterImplicits {

  import com.sksamuel.elastic4s.http.search.SearchResponse

  implicit object FlushIndexResponseConverter extends ResponseConverter[FlushResponse, FlushIndexResponse] {
    override def convert(response: FlushResponse) = FlushIndexResponse(
      Shards(response.getTotalShards, response.getFailedShards, response.getSuccessfulShards)
    )
  }

  implicit object IndexResponseConverter extends ResponseConverter[RichIndexResponse, IndexResponse] {
    override def convert(response: RichIndexResponse): IndexResponse = {
      val shardInfo = response.original.getShardInfo

      IndexResponse(
        response.id,
        response.index,
        response.`type`,
        response.version,
        response.original.getResult.getLowercase,
        response.original.forcedRefresh(),
        Shards(shardInfo.getTotal, shardInfo.getFailed, shardInfo.getSuccessful),
        response.created
      )
    }
  }

  implicit object CreateIndexResponseConverter extends ResponseConverter[TcpCreateIndexResponse, CreateIndexResponse] {
    override def convert(response: TcpCreateIndexResponse) = CreateIndexResponse(
      response.isAcknowledged, response.isShardsAcked
    )
  }

  implicit object BulkResponseConverter extends ResponseConverter[RichBulkResponse, BulkResponse] {
    override def convert(response: RichBulkResponse) = BulkResponse(
      response.took.toMillis,
      response.hasFailures,
      response.items.map { x =>
        BulkResponseItems(
          BulkResponseItem(
            x.itemId,
            x.index,
            x.`type`,
            x.id,
            x.version,
            false,
            false,
            true,
            "Created",
            x.original.status.getStatus,
            None,
            None
          ).some,
          None,
          None
        )
      }
    )
  }

  implicit object SearchResponseConverter extends ResponseConverter[RichSearchResponse, SearchResponse] {
    override def convert(response: RichSearchResponse) = SearchResponse(
      response.tookInMillis.toInt,
      response.isTimedOut,
      response.isTerminatedEarly.getOrElse(false),
      null, // TODO
      Shards(response.totalShards, response.shardFailures.length, response.successfulShards),
      Option(response.scrollId),
      null, // TODO: Aggregations are still being working on
      SearchHits(
        response.totalHits.toInt,
        response.maxScore,
        response.hits.map { x =>
          SearchHit(
            x.id,
            x.index,
            x.`type`,
            x.score,
            x.sourceAsMap.asScalaNested,
            x.fields.mapValues(_.value),
            x.highlightFields.mapValues(_.fragments.map(_.string)),
            inner_hits = Map.empty,// TODO: Set properly
            x.version
          )
        }
      )
    )
  }

  implicit object IndexExistsResponseConverter extends ResponseConverter[IndicesExistsResponse, IndexExistsResponse] {
    override def convert(response: IndicesExistsResponse) = IndexExistsResponse(response.isExists)
  }

  implicit object DeleteIndexResponseConverter extends ResponseConverter[TcpDeleteIndexResponse, DeleteIndexResponse] {
    override def convert(response: TcpDeleteIndexResponse) = DeleteIndexResponse(response.isAcknowledged)
  }

  implicit object OpenIndexResponseConverter extends ResponseConverter[TcpOpenIndexResponse, OpenIndexResponse] {
    override def convert(response: TcpOpenIndexResponse) = OpenIndexResponse(response.isAcknowledged)
  }

  implicit object CloseIndexResponseConverter extends ResponseConverter[TcpCloseIndexResponse, CloseIndexResponse] {
    override def convert(response: TcpCloseIndexResponse) = CloseIndexResponse(response.isAcknowledged)
  }

  implicit object RefreshIndexResponseConverter extends ResponseConverter[RefreshResponse, RefreshIndexResponse] {
    override def convert(response: RefreshResponse) = RefreshIndexResponse()
  }

  implicit object TypeExistsResponseConverter extends ResponseConverter[TypesExistsResponse, TypeExistsResponse] {
    override def convert(response: TypesExistsResponse) = TypeExistsResponse(response.isExists)
  }

  implicit object ClearCacheResponseConverter extends ResponseConverter[ClearIndicesCacheResponse, ClearCacheResponse] {
    override def convert(response: ClearIndicesCacheResponse) = ClearCacheResponse(
      Shards(
        response.getTotalShards,
        response.getFailedShards,
        response.getSuccessfulShards
      )
    )
  }

  implicit object DeleteResponseConverter extends ResponseConverter[TcpDeleteResponse, DeleteResponse] {
    override def convert(response: TcpDeleteResponse): DeleteResponse = {
      val shardInfo = response.getShardInfo

      DeleteResponse(
        Shards(shardInfo.getTotal, shardInfo.getFailed, shardInfo.getSuccessful),
        response.getResult == DocWriteResponse.Result.DELETED,
        response.getIndex,
        response.getType,
        response.getId,
        response.getVersion,
        response.getResult.getLowercase
      )
    }
  }

  implicit object DeleteByQueryResponseConverter extends ResponseConverter[BulkByScrollResponse, DeleteByQueryResponse] {
    override def convert(response: BulkByScrollResponse): DeleteByQueryResponse = {
      val field = classOf[BulkByScrollResponse].getDeclaredField("status")
      field.setAccessible(true)
      val status = field.get(response).asInstanceOf[BulkByScrollTask.Status]

      DeleteByQueryResponse(
        response.getTook.millis,
        response.isTimedOut,
        status.getTotal,
        response.getDeleted,
        response.getBatches,
        response.getVersionConflicts,
        response.getNoops,
        status.getThrottled.millis,
        if(status.getRequestsPerSecond == Float.PositiveInfinity) -1 else status.getRequestsPerSecond.toLong,
        status.getThrottledUntil.millis
      )
    }
  }

  implicit object GetResponseConverter extends ResponseConverter[RichGetResponse, GetResponse] {
    override def convert(response: RichGetResponse) = GetResponse(
      response.id,
      response.index,
      response.`type`,
      response.version,
      response.exists,
      response.original.getFields.asScala.toMap.mapValues(_.getValues.asScala),
      response.sourceAsMap.asScalaNested
    )
  }

  implicit object MultiGetResponseConverter extends ResponseConverter[RichMultiGetResponse, MultiGetResponse] {
    override def convert(response: RichMultiGetResponse) = MultiGetResponse(
      response.successes.map(GetResponseConverter.convert)
    )
  }

  implicit object ExplainResponseConverter extends ResponseConverter[TcpExplainResponse, ExplainResponse] {
    import com.sksamuel.elastic4s.http.explain.Explanation

    override def convert(response: TcpExplainResponse) = ExplainResponse(
      response.getIndex,
      response.getType,
      response.getId,
      response.isMatch,
      Option(response.getExplanation).map(convertExplanation).orNull
    )

    private def convertExplanation(e: org.apache.lucene.search.Explanation): Explanation = {
      Explanation(e.getValue, e.getDescription, e.getDetails.map(convertExplanation))
    }
  }

  implicit object ValidateResponseConverter extends ResponseConverter[ValidateQueryResponse, ValidateResponse] {
    import com.sksamuel.elastic4s.http.validate.Explanation

    override def convert(response: ValidateQueryResponse) = ValidateResponse(
      response.isValid,
      Shards(
        response.getTotalShards,
        response.getFailedShards,
        response.getSuccessfulShards
      ),
      response.getQueryExplanation.asScala.map(x => Explanation(x.getIndex, x.isValid, x.getError))
    )
  }

  implicit object UpdateResponseConverter extends ResponseConverter[RichUpdateResponse, UpdateResponse] {
    override def convert(response: RichUpdateResponse): UpdateResponse = {
      val shardInfo = response.shardInfo

      UpdateResponse(
        response.index,
        response.`type`,
        response.id,
        response.version,
        response.result.getLowercase,
        response.original.forcedRefresh(),
        Shards(shardInfo.getTotal, shardInfo.getFailed, shardInfo.getSuccessful)
      )
    }
  }

  implicit object ClusterHealthResponseConverter extends ResponseConverter[TcpClusterHealthResponse, ClusterHealthResponse] {
    override def convert(response: TcpClusterHealthResponse) = ClusterHealthResponse(
      response.getClusterName,
      response.getStatus.name.toLowerCase(Locale.ENGLISH),
      response.isTimedOut,
      response.getNumberOfNodes,
      response.getNumberOfDataNodes,
      response.getActivePrimaryShards,
      response.getActiveShards,
      response.getRelocatingShards,
      response.getInitializingShards,
      response.getUnassignedShards,
      response.getDelayedUnassignedShards,
      response.getNumberOfPendingTasks,
      response.getNumberOfInFlightFetch,
      response.getTaskMaxWaitingTime.millis.toInt,
      response.getActiveShardsPercent
    )
  }


  implicit object PutMappingResponseConverter extends ResponseConverter[TcpPutMappingResponse, PutMappingResponse] {
    override def convert(response: TcpPutMappingResponse) = PutMappingResponse(response.isAcknowledged)
  }

  implicit object ClearScrollResponseConverter extends ResponseConverter[ClearScrollResult, ClearScrollResponse] {
    override def convert(response: ClearScrollResult): ClearScrollResponse =
      ClearScrollResponse(response.success, response.number)
  }

  implicit class RichSourceMap(val self: Map[String, AnyRef]) extends AnyVal {
    def asScalaNested: Map[String, AnyRef] = {
      def toScala(a: AnyRef): AnyRef = a match {
        case i: java.lang.Iterable[AnyRef] => i.asScala.map(toScala)
        case m: util.Map[AnyRef, AnyRef] => m.asScala.map { case (k, v) => (toScala(k), toScala(v)) }
        case other => other
      }

      self.mapValues(toScala)
    }
  }

}

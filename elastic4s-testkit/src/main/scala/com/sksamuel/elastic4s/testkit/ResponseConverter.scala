package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.bulk.RichBulkResponse
import com.sksamuel.elastic4s.http.Shards
import com.sksamuel.elastic4s.http.bulk.{BulkResponse, BulkResponseItem, Index}
import com.sksamuel.elastic4s.http.index.{FlushIndexResponse, IndexResponse}
import com.sksamuel.elastic4s.http.search.{SearchHit, SearchHits}
import com.sksamuel.elastic4s.index.RichIndexResponse
import com.sksamuel.elastic4s.searches.RichSearchResponse
import org.elasticsearch.action.admin.indices.create.{CreateIndexResponse => TcpCreateIndexResponse}
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import org.elasticsearch.action.admin.indices.flush.FlushResponse

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
    override def convert(response: RichIndexResponse) = {
      val shardInfo = response.original.getShardInfo

      IndexResponse(
        response.id,
        response.index,
        response.`type`,
        response.version,
        response.result.toString,
        response.original.forcedRefresh(),
        true, // TODO
        0, // TODO: Where is `totalHits` in the response?
        Shards(shardInfo.getTotal, shardInfo.getFailed, shardInfo.getSuccessful),
        response.created,
        Map.empty, // TODO
        Map.empty // TODO
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
        BulkResponseItem(
          Index(
            x.index,
            x.`type`,
            x.id,
            x.version,
            "", // TODO
            false, // TODO
            null // TODO
          ))
      }
    )
  }

  implicit object SearchResponseConverter extends ResponseConverter[RichSearchResponse, SearchResponse] {
    override def convert(response: RichSearchResponse) = SearchResponse(
      response.tookInMillis.toInt,
      response.isTimedOut,
      response.isTerminatedEarly,
      null, // TODO
      Shards(response.totalShards, response.shardFailures.length, response.successfulShards),
      response.scrollId,
      null, // TODO: Aggregations are still being working on
      SearchHits(
        response.totalHits.toInt,
        response.maxScore,
        response.hits.map(x => SearchHit(
          x.id,
          x.index,
          x.`type`,
          x.score,
          x.sourceAsMap,
          x.fields,
          Map.empty, // TODO
          x.version
        ))
      ))
  }
}

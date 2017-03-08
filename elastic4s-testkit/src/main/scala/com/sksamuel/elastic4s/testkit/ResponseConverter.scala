package com.sksamuel.elastic4s.testkit

import java.util

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
    override def convert(response: RichIndexResponse) = {
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
          x.sourceAsMap.asScalaNested,
          x.fields,
          Map.empty, // TODO
          x.version
        ))
      ))
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

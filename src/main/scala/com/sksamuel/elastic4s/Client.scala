package com.sksamuel.elastic4s

import scala.concurrent._
import org.elasticsearch.client.Client
import java.util.concurrent.TimeUnit
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse
import org.elasticsearch.action.percolate.PercolateResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.admin.indices.validate.query.{ValidateQueryRequest, ValidateQueryResponse}
import org.elasticsearch.action.mlt.MoreLikeThisRequest

/** @author Stephen Samuel */
class ScalaClient(val client: org.elasticsearch.client.Client,
                  timeout: Long = 5000)
                 (implicit executionContext: ExecutionContext = ExecutionContext.global) {

    def index(req: IndexReq): Future[IndexResponse] = future {

        client
          .prepareIndex()
          .setIndex(req.index)
          .setType(req.`type`)
          .setId(req.id.orNull)
          .setOpType(req.opType)
          .setParent(req.parent.orNull)
          .setTimestamp(req.timestamp.orNull)
          .setRefresh(req.refresh)
          .setVersion(req.version)
          .setVersionType(req.versionType)
          .setSource(req._source)
          .execute()
          .actionGet(timeout, TimeUnit.MILLISECONDS)
    }

    def bulk(indexRequests: Seq[IndexRequest]): Future[BulkResponse] = future {
        val bulk = client.prepareBulk()
        indexRequests.foreach(arg => bulk.add(arg))
        bulk.execute().actionGet(timeout)
    }

    def exists(indexes: Iterable[String]) = future {
        client.admin().indices().prepareExists(indexes.toSeq: _*).execute().actionGet(timeout)
    }

    def register(idx: String, `type`: String, req: IndexReq): Future[IndexResponse] = index(req)

    def percolate(index: String, `type`: String): Future[PercolateResponse] = future {
        client.preparePercolate(index, `type`).setSource("").execute().actionGet(timeout)
    }

    def count(req: CountReq): Future[CountResponse] = future {
        client
          .prepareCount(req.indexes: _*)
          .setTypes(req.types: _*)
          .setRouting(req.routing.mkString(","))
          .execute()
          .actionGet(timeout, TimeUnit.MILLISECONDS)
    }

    def delete(req: DeleteReq): Future[DeleteResponse] = future {
        client
          .prepareDelete(req.index, req.`type`, req.id)
          .setRouting(req.routing.orNull)
          .setVersion(req.version)
          .setParent(req.parent.orNull)
          .setRefresh(req.refresh)
          .execute()
          .actionGet(timeout, TimeUnit.MILLISECONDS)
    }

    def deleteByQuery(req: DeleteByQueryReq): Future[DeleteByQueryResponse] = future {
        client
          .prepareDeleteByQuery(req.indexes: _*)
          .setRouting(req.routing.mkString(","))
          .setTypes(req.types: _*)
          .setQuery("todo") //to do
          .execute()
          .actionGet(timeout, TimeUnit.MILLISECONDS)
    }

    def search(index: String) = new SearchBuilder(Seq(index))
    def search(indexes: Seq[String]) = new SearchBuilder(indexes)

    def search(req: SearchReq): Future[SearchResponse] = future {

        val search = client.prepareSearch(req.indexes.toSeq: _*)
          .addFields(req.fields: _*)
          .setExplain(req.explain)
          .setSearchType(req.searchType.elasticType)
          .setHighlighterPreTags(req.highlight.map(_.preTags).orNull: _*)
          .setHighlighterPostTags(req.highlight.map(_.postTags).orNull: _*)
          .setRouting(req.routing.mkString(","))
          .setSize(req.size.toInt)
          .setFrom(req.from.toInt)
          .setScroll(req.scroll.orNull)
          .setTrackScores(req.trackScores)

        for ( sort <- req.sorts )
            search.addSort(sort.builder)

        for ( facet <- req.facets )
            search.addFacet(facet.builder)

        search.execute()
          .actionGet(timeout, TimeUnit.MILLISECONDS)
    }

    def searchScroll(scrollId: String): Future[SearchResponse] = future {
        client.prepareSearchScroll(scrollId).execute().actionGet(timeout)
    }

    def createIndex(req: CreateIndexReq): Future[CreateIndexResponse] = future {
        client
          .admin()
          .indices()
          .prepareCreate(req.name)
          .setSettings(req._source)
          .execute()
          .actionGet(timeout, TimeUnit.MILLISECONDS)
    }

    def validate(req: ValidateQueryRequest): Future[ValidateQueryResponse] = future {
        client.admin().indices().prepareValidateQuery("").execute().actionGet(timeout) // todo
    }

    def moreLikeThis(req: MoreLikeThisRequest): Future[SearchResponse] = future {
        client.prepareMoreLikeThis("", "", "").execute().actionGet(timeout)
    }

    def close(): Unit = client.close()
}

object ScalaClient {
    implicit def client2scala(client: Client) = apply(client)
    def apply(client: Client, timeout: Long = 5000): ScalaClient = new ScalaClient(client, timeout)
}

sealed abstract class SearchOperationThreading(elastic: org.elasticsearch.action.search.SearchOperationThreading)
object SearchOperationThreading {
    case object NoThreads extends SearchOperationThreading(org.elasticsearch.action.search.SearchOperationThreading.NO_THREADS)
    case object SingleThread extends SearchOperationThreading(org.elasticsearch.action.search.SearchOperationThreading.SINGLE_THREAD)
    case object ThreadPerShard extends SearchOperationThreading(org.elasticsearch.action.search.SearchOperationThreading.THREAD_PER_SHARD)
}


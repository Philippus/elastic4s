package com.sksamuel.elastic4s

import scala.concurrent._
import org.elasticsearch.client.Client
import java.util.concurrent.TimeUnit
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.search.sort.SortOrder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse

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

    def search(req: SearchReq): Future[SearchResponse] = future {

        val search = client.prepareSearch(req.indexes: _*)
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
            search.addSort(sort.field, if (sort.asc) SortOrder.ASC else SortOrder.DESC)

        for ( facet <- req.facets )
            search.addFacet(facet.builder)

        search.execute()
          .actionGet(timeout, TimeUnit.MILLISECONDS)
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
}

object ScalaClient {
    def apply(client: Client, timeout: Long = 5000): ScalaClient = new ScalaClient(client, timeout)
}

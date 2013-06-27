package com.sksamuel.elastic4s

import scala.concurrent._
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.action.count.{CountRequest, CountResponse}
import org.elasticsearch.action.search.{SearchRequest, SearchResponse}
import org.elasticsearch.action.percolate.PercolateResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.admin.indices.validate.query.{ValidateQueryRequest, ValidateQueryResponse}
import org.elasticsearch.action.mlt.MoreLikeThisRequest
import org.elasticsearch.common.settings.{Settings, ImmutableSettings}
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.node.{Node, NodeBuilder}
import org.elasticsearch.client.Client
import com.sksamuel.elastic4s.IndexDsl.IndexBuilder
import com.sksamuel.elastic4s.SearchDsl.SearchBuilder
import com.sksamuel.elastic4s.CountDsl.CountBuilder

/** @author Stephen Samuel */
class ElasticClient(val client: org.elasticsearch.client.Client, timeout: Long)
                   (implicit executionContext: ExecutionContext = ExecutionContext.global) {

    /**
     * Indexes a Java IndexRequest and returns a scala Future with the IndexResponse.
     *
     * @param req an IndexRequest from the Java client
     *
     * @return a Future providing an IndexResponse
     */
    def execute(req: IndexRequest): Future[IndexResponse] = future {
        client.index(req).actionGet(timeout)
    }

    /**
     * Indexes a Scala DSL IndexBuilder and returns a scala Future with the IndexResponse.
     *
     * @param builder an IndexBuilder from the Scala DSL
     *
     * @return a Future providing an IndexResponse
     */
    def execute(builder: IndexBuilder): Future[IndexResponse] = future {
        client.index(builder.java).actionGet(timeout)
    }

    /**
     * Executes a Java API SearchRequest and returns a scala Future with the SearchResponse.
     *
     * @param req a SearchRequest from the Java client
     *
     * @return a Future providing an SearchResponse
     */
    def execute(req: SearchRequest): Future[SearchResponse] = future {
        client.search(req).actionGet(timeout)
    }

    /**
     * Executes a Scala DSL search and returns a scala Future with the SearchResponse.
     *
     * @param builder a SearchBuilder from the Scala DSL
     *
     * @return a Future providing an SearchResponse
     */
    def execute(builder: SearchBuilder): Future[SearchResponse] = future {
        client.search(builder.build).actionGet(timeout)
    }

    /**
     * Executes a Java API CountRequest and returns a scala Future with the CountResponse.
     *
     * @param req a CountRequest from the Java client
     *
     * @return a Future providing an CountResponse
     */
    def execute(req: CountRequest): Future[CountResponse] = future {
        client.count(req).actionGet(timeout)
    }

    /**
     * Executes a Scala DSL search and returns a scala Future with the CountResponse.
     *
     * @param builder a CountBuilder from the Scala DSL
     *
     * @return a Future providing an CountResponse
     */
    def execute(builder: CountBuilder): Future[CountResponse] = future {
        client.count(builder.build).actionGet(timeout)
    }

    def bulk(indexRequests: Seq[IndexRequest]): Future[BulkResponse] = future {
        val bulk = client.prepareBulk()
        indexRequests.foreach(arg => bulk.add(arg))
        bulk.execute().actionGet(timeout)
    }

    def exists(indexes: Iterable[String]) = future {
        client.admin().indices().prepareExists(indexes.toSeq: _*).execute().actionGet(timeout)
    }

    def register(idx: String, `type`: String, req: IndexRequest): Future[IndexResponse] = execute(req)

    def percolate(index: String, `type`: String): Future[PercolateResponse] = future {
        client.preparePercolate(index, `type`).setSource("").execute().actionGet(timeout)
    }

    //    def delete(req: DeleteReq): Future[DeleteResponse] = future {
    //        client
    //          .prepareDelete(req.index, req.`type`, req.id)
    //          .setRouting(req.routing.orNull)
    //          .setVersion(req.version)
    //          .setParent(req.parent.orNull)
    //          .setRefresh(req.refresh)
    //          .execute()
    //          .actionGet(timeout, TimeUnit.MILLISECONDS)
    //    }
    //
    //    def deleteByQuery(req: DeleteByQueryReq): Future[DeleteByQueryResponse] = future {
    //        client
    //          .prepareDeleteByQuery(req.indexes: _*)
    //          .setRouting(req.routing.mkString(","))
    //          .setTypes(req.types: _*)
    //          .setQuery("todo") //to do
    //          .execute()
    //          .actionGet(timeout, TimeUnit.MILLISECONDS)
    //    }

    def searchScroll(scrollId: String): Future[SearchResponse] = future {
        client.prepareSearchScroll(scrollId).execute().actionGet(timeout)
    }

    //    def createIndex(req: CreateIndexReq): Future[CreateIndexResponse] = future {
    //        client
    //          .admin()
    //          .indices()
    //          .prepareCreate(req.name)
    //          .setSettings(req._source)
    //          .execute()
    //          .actionGet(timeout, TimeUnit.MILLISECONDS)
    //    }

    def validate(req: ValidateQueryRequest): Future[ValidateQueryResponse] = future {
        client.admin().indices().prepareValidateQuery("").execute().actionGet(timeout) // todo
    }

    def moreLikeThis(req: MoreLikeThisRequest): Future[SearchResponse] = future {
        client.prepareMoreLikeThis("", "", "").execute().actionGet(timeout)
    }

    def close(): Unit = client.close()
}

object ElasticClient {

    val DefaultTimeout = 5000

    def fromClient(client: Client): ElasticClient = fromClient(client, DefaultTimeout)
    def fromClient(client: Client, timeout: Long = DefaultTimeout): ElasticClient = new ElasticClient(client, timeout)
    def fromNode(node: Node): ElasticClient = fromNode(node, DefaultTimeout)
    def fromNode(node: Node, timeout: Long = DefaultTimeout): ElasticClient = fromClient(node.client, timeout)

    def remote(settings: Settings,
               host: String = "localhost",
               ports: Seq[Int] = Seq(9300),
               timeout: Long = DefaultTimeout): ElasticClient = {
        require(settings.getAsMap.containsKey("cluster.name"))
        val client = new TransportClient(settings)
        for ( port <- ports ) client.addTransportAddress(new InetSocketTransportAddress(host, port))
        fromClient(client, timeout)
    }

    def local: ElasticClient = local(ImmutableSettings.settingsBuilder().build())
    def local(settings: Settings, timeout: Long = DefaultTimeout): ElasticClient =
        fromNode(NodeBuilder.nodeBuilder().local(true).data(true).settings(settings).node())

}

sealed abstract class SearchOperationThreading(elastic: org.elasticsearch.action.search.SearchOperationThreading)
object SearchOperationThreading {
    case object NoThreads extends SearchOperationThreading(org.elasticsearch.action.search.SearchOperationThreading.NO_THREADS)
    case object SingleThread extends SearchOperationThreading(org.elasticsearch.action.search.SearchOperationThreading.SINGLE_THREAD)
    case object ThreadPerShard extends SearchOperationThreading(org.elasticsearch.action.search.SearchOperationThreading.THREAD_PER_SHARD)
}


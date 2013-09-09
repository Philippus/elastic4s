package com.sksamuel.elastic4s

import scala.concurrent._
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.action.count.{CountRequest, CountResponse}
import org.elasticsearch.action.search.{MultiSearchResponse, SearchRequest, SearchResponse}
import org.elasticsearch.action.admin.indices.validate.query.{ValidateQueryResponse, ValidateQueryRequest}
import org.elasticsearch.action.mlt.MoreLikeThisRequest
import org.elasticsearch.common.settings.{Settings, ImmutableSettings}
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.node.{Node, NodeBuilder}
import org.elasticsearch.client.Client
import org.elasticsearch.action.get.{MultiGetRequest, MultiGetResponse, GetResponse, GetRequest}
import org.elasticsearch.action.delete.{DeleteResponse, DeleteRequest}
import org.elasticsearch.action.deletebyquery.{DeleteByQueryRequest, DeleteByQueryResponse}
import org.elasticsearch.action.update.{UpdateResponse, UpdateRequest}
import scala.concurrent.duration._
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.percolate.PercolateResponse
import ElasticDsl._
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse
import org.elasticsearch.action.{ActionRequestBuilder, ActionResponse, ActionRequest, ActionListener}
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse

/** @author Stephen Samuel */
class ElasticClient(val client: org.elasticsearch.client.Client, var timeout: Long) {

  /**
   * Executes a Scala DSL RequestDefinition and returns a scala Future with corresponding ActionResponse.
   *
   * @param requestDefinition a RequestDefinition from the Scala DSL
   *
   * @return a Future providing corresponding ActionResponse
   */
  def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: RequestDefinition[Req, Res, Builder]): Future[Res] = {
    val p = Promise[Res]()
    execute(requestDefinition, new ActionListener[Res] {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: Res): Unit = p.success(response)
    })
    p.future
  }

  def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: RequestDefinition[Req, Res, Builder], callback: ActionListener[Res]): Unit =
    client.execute(requestDefinition.action, requestDefinition.build, callback)

  /**
   * Executes a Scala DSL IndicesRequestDefinition and returns a scala Future with corresponding ActionResponse.
   *
   * @param requestDefinition a RequestDefinition from the Scala DSL
   *
   * @return a Future providing corresponding ActionResponse
   */
  def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: IndicesRequestDefinition[Req, Res, Builder]): Future[Res] = {
    val p = Promise[Res]()
    execute(requestDefinition, new ActionListener[Res] {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: Res): Unit = p.success(response)
    })
    p.future
  }

  def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: IndicesRequestDefinition[Req, Res, Builder], callback: ActionListener[Res]): Unit =
    client.admin.indices.execute(requestDefinition.action, requestDefinition.build, callback)

  /**
   * Indexes a Java IndexRequest and returns a scala Future with the IndexResponse.
   *
   * @param req an IndexRequest from the Java client
   *
   * @return a Future providing an IndexResponse
   */
  def execute(req: IndexRequest): Future[IndexResponse] = {
    val p = Promise[IndexResponse]()
    client.index(req, new ActionListener[IndexResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: IndexResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(req: IndexRequest, callback: ActionListener[IndexResponse]) = client.index(req, callback)

  /**
   * Executes a Java API SearchRequest and returns a scala Future with the SearchResponse.
   *
   * @param req a SearchRequest from the Java client
   *
   * @return a Future providing an SearchResponse
   */
  def execute(req: SearchRequest): Future[SearchResponse] = {
    val p = Promise[SearchResponse]()
    client.search(req, new ActionListener[SearchResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: SearchResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(req: SearchRequest, callback: ActionListener[SearchResponse]) = client.search(req, callback)

  /**
   * Executes a Scala DSL search and returns a scala Future with the SearchResponse.
   *
   * @param sdef a SearchDefinition from the Scala DSL
   *
   * @return a Future providing an SearchResponse
   */
  def search(sdef: SearchDefinition): Future[SearchResponse] = execute(sdef.build)

  @deprecated("use the sync client")
  def result(search: SearchDefinition)(implicit duration: Duration): SearchResponse =
    Await.result(execute(search), duration)

  def search(searches: SearchDefinition*): Future[MultiSearchResponse] =
    execute(new MultiSearchDefinition(searches))

  /**
   * Executes a Java API CountRequest and returns a scala Future with the CountResponse.
   *
   * @param req a CountRequest from the Java client
   *
   * @return a Future providing an CountResponse
   */
  def execute(req: CountRequest): Future[CountResponse] = {
    val p = Promise[CountResponse]()
    client.count(req, new ActionListener[CountResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: CountResponse): Unit = p.success(response)
    })
    p.future
  }

  /**
   * Executes a Java API GetRequest and returns a scala Future with the GetResponse.
   *
   * @param req a GetRequest from the Java client
   *
   * @return a Future providing an GetResponse
   */
  def get(req: GetRequest): Future[GetResponse] = {
    val p = Promise[GetResponse]()
    client.get(req, new ActionListener[GetResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: GetResponse): Unit = p.success(response)
    })
    p.future
  }

  /**
   * Executes a Scala DSL get and returns a scala Future with the GetResponse.
   *
   * @param builder a GetDefinition from the Scala DSL
   *
   * @return a Future providing an GetResponse
   */
  @deprecated
  def execute(builder: GetDefinition): Future[GetResponse] = get(builder)
  def get(builder: GetDefinition): Future[GetResponse] = get(builder.build)

  def get(req: MultiGetRequest): Future[MultiGetResponse] = {
    val p = Promise[MultiGetResponse]()
    client.multiGet(req, new ActionListener[MultiGetResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: MultiGetResponse): Unit = p.success(response)
    })
    p.future
  }

  def get(gets: GetDefinition*): Future[MultiGetResponse] = execute(new MultiGetDefinition(gets))

  def delete(d: DeleteByIdDefinition): Future[DeleteResponse] = execute(d)

  def optimize(d: OptimizeDefinition): Future[OptimizeResponse] = execute(d)

  def delete(d: DeleteByQueryDefinition): Future[DeleteByQueryResponse] = execute(d)

  def execute(req: ValidateQueryRequest): Future[ValidateQueryResponse] = {
    val p = Promise[ValidateQueryResponse]()
    client.admin.indices().validateQuery(req, new ActionListener[ValidateQueryResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: ValidateQueryResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(req: UpdateRequest): Future[UpdateResponse] = {
    val p = Promise[UpdateResponse]()
    client.update(req, new ActionListener[UpdateResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: UpdateResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(req: MoreLikeThisRequest): Future[SearchResponse] = {
    val p = Promise[SearchResponse]()
    client.moreLikeThis(req, new ActionListener[SearchResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: SearchResponse): Unit = p.success(response)
    })
    p.future
  }

  def bulk(requests: BulkCompatibleDefinition*): Future[BulkResponse] = {
    val bulk = client.prepareBulk()
    requests.foreach(req => req match {
      case index: IndexDefinition => bulk.add(index.build)
      case delete: DeleteByIdDefinition => bulk.add(delete.build)
      case update: UpdateDefinition => bulk.add(update.build)
    })
    val p = Promise[BulkResponse]()
    bulk.execute(new ActionListener[BulkResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: BulkResponse): Unit = p.success(response)
    })
    p.future
  }

  @deprecated("use the sync client")
  def result(requests: BulkCompatibleDefinition*)(implicit duration: Duration): BulkResponse =
    Await.result(bulk(requests: _*), duration)

  def exists(indexes: String*): Future[IndicesExistsResponse] = {
    val p = Promise[IndicesExistsResponse]()
    client.admin().indices().prepareExists(indexes.toSeq: _*).execute(new ActionListener[IndicesExistsResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: IndicesExistsResponse): Unit = p.success(response)
    })
    p.future
  }

  def register(registerDef: RegisterDefinition): Future[IndexResponse] = execute(registerDef)

  def percolate(percolate: PercolateDefinition): Future[PercolateResponse] = execute(percolate)

  def deleteIndex(d: DeleteIndexDefinition): Future[DeleteIndexResponse] = execute(d)

  def searchScroll(scrollId: String): Future[SearchResponse] = {
    val p = Promise[SearchResponse]()
    client.prepareSearchScroll(scrollId).execute(new ActionListener[SearchResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: SearchResponse): Unit = p.success(response)
    })
    p.future
  }

  def flush(indexes: String*): Future[FlushResponse] = {
    val p = Promise[FlushResponse]()
    client.admin().indices().prepareFlush(indexes: _*).execute(new ActionListener[FlushResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: FlushResponse): Unit = p.success(response)
    })
    p.future
  }

  def refresh(indexes: String*): Future[RefreshResponse] = {
    val p = Promise[RefreshResponse]()
    client.admin().indices().prepareRefresh(indexes: _*).execute(new ActionListener[RefreshResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: RefreshResponse): Unit = p.success(response)
    })
    p.future
  }

  def optimize(indexes: String*): Future[OptimizeResponse] = {
    val p = Promise[OptimizeResponse]()
    client.admin().indices().prepareOptimize(indexes: _*).execute(new ActionListener[OptimizeResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: OptimizeResponse): Unit = p.success(response)
    })
    p.future
  }

  def open(index: String): Future[OpenIndexResponse] = {
    val p = Promise[OpenIndexResponse]()
    client.admin().indices().prepareOpen(index).execute(new ActionListener[OpenIndexResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: OpenIndexResponse): Unit = p.success(response)
    })
    p.future
  }

  def close(index: String): Future[CloseIndexResponse] = {
    val p = Promise[CloseIndexResponse]()
    client.admin().indices().prepareClose(index).execute(new ActionListener[CloseIndexResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: CloseIndexResponse): Unit = p.success(response)
    })
    p.future
  }

  def segments(indexes: String*): Future[IndicesSegmentResponse] = {
    val p = Promise[IndicesSegmentResponse]()
    client.admin().indices().prepareSegments(indexes: _*).execute(new ActionListener[IndicesSegmentResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: IndicesSegmentResponse): Unit = p.success(response)
    })
    p.future
  }

  def close(): Unit = client.close()

  def java = client
  def admin = client.admin

  def sync(implicit duration: Duration = 10.seconds) = new SyncClient(this)(duration)

  class SyncClient(client: ElasticClient)(implicit duration: Duration) {

    def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: RequestDefinition[Req, Res, Builder]): Res =
      Await.result(client.execute(requestDefinition), duration)

    def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: IndicesRequestDefinition[Req, Res, Builder]): Res =
      Await.result(client.execute(requestDefinition), duration)

    def deleteIndex(deleteIndex: DeleteIndexDefinition)(implicit duration: Duration): DeleteIndexResponse =
      Await.result(client.deleteIndex(deleteIndex), duration)

    def percolate(percolateDef: PercolateDefinition)(implicit duration: Duration): PercolateResponse =
      Await.result(client.percolate(percolateDef), duration)

    def register(registerDef: RegisterDefinition)(implicit duration: Duration): IndexResponse =
      Await.result(client.register(registerDef), duration)

    def delete(ddef: DeleteByIdDefinition)(implicit duration: Duration): DeleteResponse =
      Await.result(client.delete(ddef), duration)

    def delete(ddef: DeleteByQueryDefinition)(implicit duration: Duration): DeleteByQueryResponse =
      Await.result(client.delete(ddef), duration)

    def get(get: GetDefinition)(implicit duration: Duration): GetResponse = execute(get)

    def search(sdef: SearchDefinition)(implicit duration: Duration): SearchResponse =
      Await.result(client.search(sdef), duration)

    def search(searches: SearchDefinition*)(implicit duration: Duration): MultiSearchResponse =
      Await.result(client.search(searches: _*), duration)

    def optimize(o: OptimizeDefinition)(implicit duration: Duration): OptimizeResponse =
      Await.result(client.optimize(o), duration)

    def get(gets: GetDefinition*)(implicit duration: Duration): MultiGetResponse =
      Await.result(client.get(gets: _*), duration)

    def exists(indexes: String*): IndicesExistsResponse = Await.result(client.exists(indexes: _*), duration)
  }
}

object ElasticClient {

  val DefaultTimeout = 5000

  def fromClient(client: Client): ElasticClient = fromClient(client, DefaultTimeout)
  def fromClient(client: Client, timeout: Long = DefaultTimeout): ElasticClient = new ElasticClient(client, timeout)
  def fromNode(node: Node): ElasticClient = fromNode(node, DefaultTimeout)
  def fromNode(node: Node, timeout: Long = DefaultTimeout): ElasticClient = fromClient(node.client, timeout)

  def remote(host: String, port: Int): ElasticClient = remote((host, port))
  def remote(addresses: (String, Int)*): ElasticClient =
    remote(ImmutableSettings.builder().build(), addresses: _*)

  def remote(settings: Settings, addresses: (String, Int)*): ElasticClient = {
    val client = new TransportClient(settings)
    for ( address <- addresses ) client.addTransportAddress(new InetSocketTransportAddress(address._1, address._2))
    fromClient(client, DefaultTimeout)
  }

  def local: ElasticClient = local(ImmutableSettings.settingsBuilder().build())
  def local(settings: Settings, timeout: Long = DefaultTimeout): ElasticClient =
    fromNode(NodeBuilder.nodeBuilder().local(true).data(true).settings(settings).node())

}

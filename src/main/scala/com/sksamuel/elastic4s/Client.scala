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
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse

/** @author Stephen Samuel */
class ElasticClient(val client: org.elasticsearch.client.Client, var timeout: Long) {

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
   * Indexes a Scala DSL IndexDefinition and returns a scala Future with the IndexResponse.
   *
   * @param builder an IndexDefinition from the Scala DSL
   *
   * @return a Future providing an IndexResponse
   */
  def execute(builder: IndexDefinition): Future[IndexResponse] = {
    val p = Promise[IndexResponse]()
    client.index(builder.build, new ActionListener[IndexResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: IndexResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(req: IndexDefinition, callback: ActionListener[IndexResponse]) = client.index(req.build, callback)

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
  def execute(sdef: SearchDefinition): Future[SearchResponse] = search(sdef)
  def search(sdef: SearchDefinition): Future[SearchResponse] = execute(sdef.build)

  @deprecated("use the sync client")
  def result(search: SearchDefinition)(implicit duration: Duration): SearchResponse =
    Await.result(execute(search), duration)

  def search(searches: SearchDefinition*): Future[MultiSearchResponse] = {
    val p = Promise[MultiSearchResponse]()
    client.multiSearch(new MultiSearchDefinition(searches).build, new ActionListener[MultiSearchResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: MultiSearchResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(sdef: SearchDefinition, callback: ActionListener[SearchResponse]) = client.search(sdef.build, callback)

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
   * Executes a Scala DSL search and returns a scala Future with the CountResponse.
   *
   * @param builder a CountDefinition from the Scala DSL
   *
   * @return a Future providing an CountResponse
   */
  def execute(builder: CountDefinition): Future[CountResponse] = execute(builder.build)

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

  def get(gets: GetDefinition*): Future[MultiGetResponse] = get(new MultiGetDefinition(gets).build)

  def execute(req: DeleteRequest): Future[DeleteResponse] = {
    val p = Promise[DeleteResponse]()
    client.delete(req, new ActionListener[DeleteResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: DeleteResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(d: DeleteByIdDefinition): Future[DeleteResponse] = delete(d)
  def delete(d: DeleteByIdDefinition): Future[DeleteResponse] = execute(d.builder)

  def execute(create: CreateIndexDefinition): Future[CreateIndexResponse] = {
    val p = Promise[CreateIndexResponse]()
    client.admin.indices.create(create.build, new ActionListener[CreateIndexResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: CreateIndexResponse): Unit = p.success(response)
    })
    p.future
  }

  def optimize(d: OptimizeDefinition): Future[OptimizeResponse] = {
    val p = Promise[OptimizeResponse]()
    client.admin().indices().optimize(d.builder, new ActionListener[OptimizeResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: OptimizeResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(req: DeleteByQueryRequest): Future[DeleteByQueryResponse] = {
    val p = Promise[DeleteByQueryResponse]()
    client.deleteByQuery(req, new ActionListener[DeleteByQueryResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: DeleteByQueryResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(d: DeleteByQueryDefinition): Future[DeleteByQueryResponse] = delete(d)
  def delete(d: DeleteByQueryDefinition): Future[DeleteByQueryResponse] = execute(d.builder)

  def execute(req: ValidateQueryRequest): Future[ValidateQueryResponse] = {
    val p = Promise[ValidateQueryResponse]()
    client.admin.indices().validateQuery(req, new ActionListener[ValidateQueryResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: ValidateQueryResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(validateDef: ValidateDefinition): Future[ValidateQueryResponse] = execute(validateDef.build)

  def execute(req: UpdateRequest): Future[UpdateResponse] = {
    val p = Promise[UpdateResponse]()
    client.update(req, new ActionListener[UpdateResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: UpdateResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(updateDef: UpdateDefinition): Future[UpdateResponse] = execute(updateDef.build)

  def execute(req: MoreLikeThisRequest): Future[SearchResponse] = {
    val p = Promise[SearchResponse]()
    client.moreLikeThis(req, new ActionListener[SearchResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: SearchResponse): Unit = p.success(response)
    })
    p.future
  }

  def execute(mltDef: MoreLikeThisDefinition): Future[SearchResponse] = execute(mltDef._builder)

  def execute(requests: BulkCompatibleRequest*): Future[BulkResponse] = {
    val bulk = client.prepareBulk()
    requests.foreach(req => req match {
      case index: IndexDefinition => bulk.add(index.build)
      case delete: DeleteByIdDefinition => bulk.add(delete.builder)
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
  def result(requests: BulkCompatibleRequest*)(implicit duration: Duration): BulkResponse =
    Await.result(execute(requests: _*), duration)

  def exists(indexes: String*): Future[IndicesExistsResponse] = {
    val p = Promise[IndicesExistsResponse]()
    client.admin().indices().prepareExists(indexes.toSeq: _*).execute(new ActionListener[IndicesExistsResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: IndicesExistsResponse): Unit = p.success(response)
    })
    p.future
  }

  def register(registerDef: RegisterDefinition): Future[IndexResponse] = execute(registerDef.build.request)

  def percolate(percolate: PercolateDefinition): Future[PercolateResponse] = {
    val p = Promise[PercolateResponse]()
    client.percolate(percolate.build, new ActionListener[PercolateResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: PercolateResponse): Unit = p.success(response)
    })
    p.future
  }

  def deleteIndex(d: DeleteIndexDefinition): Future[DeleteIndexResponse] = {
    val p = Promise[DeleteIndexResponse]()
    client.admin().indices().delete(d.builder, new ActionListener[DeleteIndexResponse]() {
      def onFailure(e: Throwable): Unit = p.failure(e)
      def onResponse(response: DeleteIndexResponse): Unit = p.success(response)
    })
    p.future
  }

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

    def execute(get: GetDefinition)(implicit duration: Duration): GetResponse = Await.result(client.get(get), duration)
    def get(get: GetDefinition)(implicit duration: Duration): GetResponse = execute(get)

    def execute(count: CountDefinition)(implicit duration: Duration): CountResponse =
      Await.result(client.execute(count), duration)

    def execute(sdef: SearchDefinition)(implicit duration: Duration): SearchResponse = search(sdef)
    def search(sdef: SearchDefinition)(implicit duration: Duration): SearchResponse =
      Await.result(client.search(sdef), duration)

    def search(searches: SearchDefinition*)(implicit duration: Duration): MultiSearchResponse =
      Await.result(client.search(searches: _*), duration)

    def execute(update: UpdateDefinition)(implicit duration: Duration): UpdateResponse =
      Await.result(client.execute(update), duration)

    def execute(index: IndexDefinition)(implicit duration: Duration): IndexResponse =
      Await.result(client.execute(index), duration)

    def execute(create: CreateIndexDefinition)(implicit duration: Duration): CreateIndexResponse =
      Await.result(client.execute(create), duration)

    def execute(mlt: MoreLikeThisDefinition)(implicit duration: Duration): SearchResponse =
      Await.result(client.execute(mlt), duration)

    def execute(v: ValidateDefinition)(implicit duration: Duration): ValidateQueryResponse =
      Await.result(client.execute(v), duration)

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

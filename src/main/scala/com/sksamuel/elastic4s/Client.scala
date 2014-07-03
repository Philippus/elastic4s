package com.sksamuel.elastic4s

import scala.concurrent._
import org.elasticsearch.action.index.{ IndexRequest, IndexResponse }
import org.elasticsearch.action.count.{ CountRequest, CountResponse }
import org.elasticsearch.action.explain.ExplainResponse
import org.elasticsearch.action.search.{ MultiSearchResponse, SearchRequest, SearchResponse }
import org.elasticsearch.action.admin.indices.validate.query.{ ValidateQueryResponse, ValidateQueryRequest }
import org.elasticsearch.action.mlt.MoreLikeThisRequest
import org.elasticsearch.common.settings.{ Settings, ImmutableSettings }
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.node.{ Node, NodeBuilder }
import org.elasticsearch.client.Client
import org.elasticsearch.action.get._
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse
import org.elasticsearch.action.update.{ UpdateResponse, UpdateRequest }
import scala.concurrent.duration._
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.percolate.PercolateResponse
import ElasticDsl._
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse
import org.elasticsearch.action.{ ActionRequestBuilder, ActionResponse, ActionRequest, ActionListener }
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import com.sksamuel.elastic4s.mappings.{ GetMappingDefinition, MappingDefinition }
import org.elasticsearch.action.admin.cluster.node.shutdown.NodesShutdownResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import com.sksamuel.elastic4s.source.DocumentSource

/** @author Stephen Samuel */
class ElasticClient(val client: org.elasticsearch.client.Client, var timeout: Long) {

  def shutdown: Future[NodesShutdownResponse] = shutdown("_local")
  def shutdown(nodeIds: String*): Future[NodesShutdownResponse] = {
    injectFuture[NodesShutdownResponse](java.admin.cluster.prepareNodesShutdown(nodeIds: _*).execute)
  }

  /** Executes a Scala DSL RequestDefinition and returns a scala Future with corresponding ActionResponse.
    *
    * @param requestDefinition a RequestDefinition from the Scala DSL
    *
    * @return a Future providing corresponding ActionResponse
    */
  def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: RequestDefinition[Req, Res, Builder]): Future[Res] =
    injectFuture[Res](execute(requestDefinition, _))

  def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: RequestDefinition[Req, Res, Builder],
                                                                                                                    callback: ActionListener[Res]): Unit =
    client.execute(requestDefinition.action, requestDefinition.build, callback)

  /** Executes a Scala DSL IndicesRequestDefinition and returns a scala Future with corresponding ActionResponse.
    *
    * @param requestDefinition a RequestDefinition from the Scala DSL
    *
    * @return a Future providing corresponding ActionResponse
    */
  def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: IndicesRequestDefinition[Req, Res, Builder]): Future[Res] =
    injectFuture[Res](execute(requestDefinition, _))

  def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: IndicesRequestDefinition[Req, Res, Builder],
                                                                                                                    callback: ActionListener[Res]): Unit =
    client.admin.indices.execute(requestDefinition.action, requestDefinition.build, callback)

  /** Executes a Scala DSL ClusterRequestDefinition and returns a scala Future with corresponding ActionResponse.
    *
    * @param requestDefinition a RequestDefinition from the Scala DSL
    *
    * @return a Future providing corresponding ActionResponse
    */
  def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: ClusterRequestDefinition[Req, Res, Builder]): Future[Res] =
    injectFuture[Res](execute(requestDefinition, _))

  def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: ClusterRequestDefinition[Req, Res, Builder],
                                                                                                                    callback: ActionListener[Res]): Unit =
    client.admin.cluster.execute(requestDefinition.action, requestDefinition.build, callback)

  /** Indexes a Java IndexRequest and returns a scala Future with the IndexResponse.
    *
    * @param req an IndexRequest from the Java client
    *
    * @return a Future providing an IndexResponse
    */
  def execute(req: IndexRequest): Future[IndexResponse] = injectFuture[IndexResponse](client.index(req, _))

  def execute(req: IndexRequest, callback: ActionListener[IndexResponse]) = client.index(req, callback)

  /** Executes a Java API SearchRequest and returns a scala Future with the SearchResponse.
    *
    * @param req a SearchRequest from the Java clientl
    *
    * @return a Future providing an SearchResponse
    */
  def execute(req: SearchRequest): Future[SearchResponse] = injectFuture[SearchResponse](client.search(req, _))

  def execute(req: SearchRequest, callback: ActionListener[SearchResponse]) = client.search(req, callback)

  /** Executes a Scala DSL search and returns a scala Future with the SearchResponse.
    *
    * @param sdef a SearchDefinition from the Scala DSL
    *
    * @return a Future providing an SearchResponse
    */
  @deprecated("use execute method", "1.0")
  def search(sdef: SearchDefinition): Future[SearchResponse] = execute(sdef.build)

  @deprecated("use execute method", "1.0")
  def search(searches: SearchDefinition*): Future[MultiSearchResponse] =
    execute(new MultiSearchDefinition(searches))

  /** Executes a Java API CountRequest and returns a scala Future with the CountResponse.
    *
    * @param req a CountRequest from the Java client
    *
    * @return a Future providing an CountResponse
    */
  def execute(req: CountRequest): Future[CountResponse] = injectFuture[CountResponse](client.count(req, _))

  /** Executes a Java API GetRequest and returns a scala Future with the GetResponse.
    *
    * @param req a GetRequest from the Java client
    *
    * @return a Future providing an GetResponse
    */
  def get(req: GetRequest): Future[GetResponse] = injectFuture[GetResponse](client.get(req, _))

  /** Executes a Scala DSL get and returns a scala Future with the GetResponse.
    *
    * @param builder a GetDefinition from the Scala DSL
    *
    * @return a Future providing an GetResponse
    */
  def execute(builder: GetDefinition): Future[GetResponse] = get(builder.build)
  @deprecated("use execute method", "1.0")
  def get(builder: GetDefinition): Future[GetResponse] = get(builder.build)

  @deprecated("use execute method with multiget block", "1.0")
  def get(req: MultiGetDefinition): Future[MultiGetResponse] = execute(req)
  @deprecated("use execute method with multiget block", "1.0")
  def get(gets: GetDefinition*): Future[MultiGetResponse] = execute(gets: _*)
  def execute(gets: GetDefinition*): Future[MultiGetResponse] = execute(new MultiGetDefinition(gets))

  @deprecated("use execute method", "1.0")
  def delete(d: DeleteByIdDefinition): Future[DeleteResponse] = execute(d)

  @deprecated("use execute method", "1.0")
  def delete(d: DeleteByQueryDefinition): Future[DeleteByQueryResponse] = execute(d)

  def execute(req: ValidateQueryRequest): Future[ValidateQueryResponse] =
    injectFuture[ValidateQueryResponse](client.admin.indices.validateQuery(req, _))

  def execute(req: UpdateRequest): Future[UpdateResponse] = injectFuture[UpdateResponse](client.update(req, _))

  def execute(req: MoreLikeThisRequest): Future[SearchResponse] =
    injectFuture[SearchResponse](client.moreLikeThis(req, _))

  def execute(req: MultiGetDefinition): Future[MultiGetResponse] = {
    injectFuture[MultiGetResponse](client.multiGet(req.build, _))
  }

  def execute(req: ExplainDefinition): Future[ExplainResponse] = {
    injectFuture[ExplainResponse](client.explain(req.build, _))
  }

  def execute(put: PutMappingDefinition): Future[PutMappingResponse] = {
    injectFuture[PutMappingResponse](client.admin.indices.putMapping(put.build, _))
  }

  @deprecated("Use bulk dsl with execute method", "1.3")
  def bulk(requests: BulkCompatibleDefinition*): Future[BulkResponse] = {
    val bulk = client.prepareBulk()
    requests.foreach {
      case index: IndexDefinition => bulk.add(index.build)
      case delete: DeleteByIdDefinition => bulk.add(delete.build)
      case update: UpdateDefinition => bulk.add(update.build)
    }
    injectFuture[BulkResponse](bulk.execute)
  }

  def execute(bulk: BulkDefinition): Future[BulkResponse] = {
    injectFuture[BulkResponse](client.bulk(bulk._builder.request, _))
  }

  @deprecated("use the sync client", "0.90.5")
  def result(requests: BulkCompatibleDefinition*)(implicit duration: Duration): BulkResponse =
    Await.result(bulk(requests: _*), duration)

  def exists(indexes: String*): Future[IndicesExistsResponse] =
    injectFuture[IndicesExistsResponse](client.admin.indices.prepareExists(indexes: _*).execute)

  @deprecated("use execute method", "1.0")
  def register(registerDef: RegisterDefinition): Future[IndexResponse] = execute(registerDef)

  @deprecated("use execute method", "1.0")
  def percolate(percolate: PercolateDefinition): Future[PercolateResponse] = execute(percolate)

  def searchScroll(scrollId: String) =
    injectFuture[SearchResponse](client.prepareSearchScroll(scrollId).execute)

  def searchScroll(scrollId: String, keepAlive: String) =
    injectFuture[SearchResponse](client.prepareSearchScroll(scrollId).setScroll(keepAlive).execute)

  def flush(indexes: String*): Future[FlushResponse] =
    injectFuture[FlushResponse](client.admin.indices.prepareFlush(indexes: _*).execute)

  def refresh(indexes: String*): Future[RefreshResponse] =
    injectFuture[RefreshResponse](client.admin.indices.prepareRefresh(indexes: _*).execute)

  def open(index: String): Future[OpenIndexResponse] =
    injectFuture[OpenIndexResponse](client.admin.indices.prepareOpen(index).execute)

  def execute(get: GetMappingDefinition): Future[GetMappingsResponse] = {
    injectFuture[GetMappingsResponse](client.admin().indices().prepareGetMappings(get.indexes: _*).execute)
  }

  def close(): Unit = client.close()

  def close(index: String): Future[CloseIndexResponse] =
    injectFuture[CloseIndexResponse](client.admin.indices.prepareClose(index).execute)

  def segments(indexes: String*): Future[IndicesSegmentResponse] =
    injectFuture[IndicesSegmentResponse](client.admin.indices.prepareSegments(indexes: _*).execute)

  def putMapping(indexes: String*)(mapping: MappingDefinition) =
    injectFuture[PutMappingResponse](client.admin.indices.preparePutMapping(indexes: _*)
      .setType(mapping.`type`).setSource(mapping.build).execute)

  def reindex(sourceIndex: String, targetIndex: String, chunkSize: Int = 500, scroll: String = "5m")(implicit ec: ExecutionContext): Future[Unit] = {
    execute {
      ElasticDsl.search in sourceIndex limit chunkSize scroll scroll searchType SearchType.Scan query matchall
    } flatMap { response =>

      def _scroll(scrollId: String): Future[Unit] = {
        searchScroll(scrollId, scroll) flatMap { response =>
          val hits = response.getHits.hits
          if (hits.length > 0) {
            hits.map(_.sourceAsString).grouped(chunkSize).foreach { sources =>
              bulk {
                sources map { source =>
                  index into targetIndex doc (new {
                    val json = source
                  } with DocumentSource)
                }: _*
              }
            }
            _scroll(response.getScrollId)
          } else {
            Future.successful()
          }
        }
      }

      val scrollId = response.getScrollId
      _scroll(scrollId)
    }
  }

  def java = client
  def admin = client.admin

  def sync(implicit duration: Duration = 10.seconds) = new SyncClient(this)(duration)

  class SyncClient(client: ElasticClient)(implicit duration: Duration) {

    def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: RequestDefinition[Req, Res, Builder]): Res =
      Await.result(client.execute(requestDefinition), duration)

    def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: IndicesRequestDefinition[Req, Res, Builder]): Res =
      Await.result(client.execute(requestDefinition), duration)

    def execute[Req <: ActionRequest[Req], Res <: ActionResponse, Builder <: ActionRequestBuilder[Req, Res, Builder]](requestDefinition: ClusterRequestDefinition[Req, Res, Builder]): Res =
      Await.result(client.execute(requestDefinition), duration)

    @deprecated("use execute method", "1.0")
    def percolate(percolateDef: PercolateDefinition)(implicit duration: Duration): PercolateResponse =
      Await.result(client.percolate(percolateDef), duration)

    @deprecated("use execute method", "1.0")
    def register(registerDef: RegisterDefinition)(implicit duration: Duration): IndexResponse =
      Await.result(client.register(registerDef), duration)

    @deprecated("use execute method", "1.0")
    def delete(ddef: DeleteByIdDefinition)(implicit duration: Duration): DeleteResponse =
      Await.result(client.delete(ddef), duration)

    @deprecated("use execute method", "1.0")
    def delete(ddef: DeleteByQueryDefinition)(implicit duration: Duration): DeleteByQueryResponse =
      Await.result(client.delete(ddef), duration)

    @deprecated("use execute method", "1.0")
    def get(get: GetDefinition)(implicit duration: Duration): GetResponse = execute(get)
    @deprecated("use execute method", "1.0")
    def get(gets: GetDefinition*)(implicit duration: Duration): MultiGetResponse =
      Await.result(client.get(gets: _*), duration)

    def search(searches: SearchDefinition)(implicit duration: Duration): SearchResponse = execute(searches)
    def execute(search: SearchDefinition)(implicit duration: Duration): SearchResponse =
      Await.result(client.execute(search), duration)

    def execute(searches: SearchDefinition*)(implicit duration: Duration): MultiSearchResponse =
      Await.result(client.execute(new MultiSearchDefinition(searches)), duration)

    def execute(o: OptimizeDefinition)(implicit duration: Duration): OptimizeResponse =
      Await.result(client.execute(o), duration)

    def execute(definition: ExplainDefinition)(implicit duration: Duration): ExplainResponse =
      Await.result(client.execute(definition), duration)

    def exists(indexes: String*): IndicesExistsResponse = Await.result(client.exists(indexes: _*), duration)

    def reindex(sourceIndex: String, targetIndex: String, chunkSize: Int = 500, scroll: String = "5m")(implicit ec: ExecutionContext, duration: Duration): Unit = {
      Await.result(client.reindex(sourceIndex, targetIndex, chunkSize, scroll), duration)
    }

    def execute(get: GetMappingDefinition)(implicit duration: Duration): GetMappingsResponse = {
      Await.result(client.execute(get), duration)
    }

    def execute(put: PutMappingDefinition)(implicit duration: Duration): PutMappingResponse = {
      Await.result(client.execute(put), duration)
    }

    def bulk(requests: BulkCompatibleDefinition*)(implicit duration: Duration): BulkResponse = {
      Await.result(client.bulk(requests: _*), duration)
    }
  }

  private def injectFuture[A](f: ActionListener[A] => Unit) = {
    val p = Promise[A]()
    f(new ActionListener[A] {
      def onFailure(e: Throwable): Unit = p.tryFailure(e)
      def onResponse(response: A): Unit = p.trySuccess(response)
    })
    p.future
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
    for (address <- addresses) client.addTransportAddress(new InetSocketTransportAddress(address._1, address._2))
    fromClient(client, DefaultTimeout)
  }

  def local: ElasticClient = local(ImmutableSettings.settingsBuilder().build())
  def local(settings: Settings, timeout: Long = DefaultTimeout): ElasticClient =
    fromNode(NodeBuilder.nodeBuilder().local(true).data(true).settings(settings).node(), timeout)

}

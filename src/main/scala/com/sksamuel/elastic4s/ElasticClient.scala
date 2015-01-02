package com.sksamuel.elastic4s

import java.net.URI

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.mappings.{GetMappingDefinition, MappingDefinition}
import com.sksamuel.elastic4s.source.StringDocumentSource
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse
import org.elasticsearch.action.{ActionFuture, ActionListener}
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse
import org.elasticsearch.action.admin.cluster.node.shutdown.NodesShutdownResponse
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse
import org.elasticsearch.action.admin.indices.alias.get.{GetAliasesRequest, GetAliasesResponse}
import org.elasticsearch.action.admin.indices.alias.{IndicesAliasesRequest, IndicesAliasesResponse}
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.create.{CreateIndexRequest, CreateIndexResponse}
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.optimize.{OptimizeRequest, OptimizeResponse}
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.recovery.RecoveryResponse
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse
import org.elasticsearch.action.admin.indices.validate.query.{ValidateQueryRequest, ValidateQueryResponse}
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.count.{CountRequest, CountResponse}
import org.elasticsearch.action.delete.{DeleteRequest, DeleteResponse}
import org.elasticsearch.action.deletebyquery.{DeleteByQueryRequest, DeleteByQueryResponse}
import org.elasticsearch.action.explain.ExplainResponse
import org.elasticsearch.action.get._
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.action.mlt.MoreLikeThisRequest
import org.elasticsearch.action.percolate.PercolateResponse
import org.elasticsearch.action.search.{MultiSearchRequest, MultiSearchResponse, SearchRequest, SearchResponse}
import org.elasticsearch.action.update.{UpdateRequest, UpdateResponse}
import org.elasticsearch.bootstrap.Elasticsearch
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.{ImmutableSettings, Settings}
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.{Node, NodeBuilder}

import scala.concurrent._
import scala.concurrent.duration._

/** @author Stephen Samuel */
class ElasticClient(val client: org.elasticsearch.client.Client) {

  def shutdown: Future[NodesShutdownResponse] = shutdown("_local")
  def shutdown(nodeIds: String*): Future[NodesShutdownResponse] = {
    injectFuture[NodesShutdownResponse](java.admin.cluster.prepareNodesShutdown(nodeIds: _*).execute)
  }

  /** Indexes a Java IndexRequest and returns a scala Future with the IndexResponse.
    *
    * @param req an IndexRequest from the Java client
    *
    * @return a Future providing an IndexResponse
    */
  def execute(req: IndexRequest): Future[IndexResponse] = injectFuture[IndexResponse](client.index(req, _))
  def execute(index: IndexDefinition): Future[IndexResponse] = execute(index.build)

  /** Executes a Java API SearchRequest and returns a scala Future with the SearchResponse.
    *
    * @param req a SearchRequest from the Java clientl
    *
    * @return a Future providing an SearchResponse
    */
  def execute(req: SearchRequest): Future[SearchResponse] = injectFuture[SearchResponse](client.search(req, _))
  def execute(req: SearchDefinition): Future[SearchResponse] = execute(req.build)

  def execute(req: DeleteRequest): Future[DeleteResponse] = injectFuture[DeleteResponse](client.delete(req, _))
  def execute(req: DeleteByIdDefinition): Future[DeleteResponse] = execute(req.build)

  def execute(req: GetAliasesRequest): Future[GetAliasesResponse] =
    injectFuture[GetAliasesResponse](client.admin.indices.getAliases(req, _))
  def execute(req: GetAliasDefinition): Future[GetAliasesResponse] = execute(req.build)

  def execute(req: IndicesAliasesRequest): Future[IndicesAliasesResponse] =
    injectFuture[IndicesAliasesResponse](client.admin.indices.aliases(req, _))
  def execute(req: MutateAliasDefinition): Future[IndicesAliasesResponse] = execute(req.build)
  def execute(req: IndicesAliasesRequestDefinition): Future[IndicesAliasesResponse] = execute(req.build)

  def execute(searches: MultiSearchRequest): Future[MultiSearchResponse] =
    injectFuture[MultiSearchResponse](client.multiSearch(searches, _))
  def execute(searches: MultiSearchDefinition): Future[MultiSearchResponse] = execute(searches.build)
  def execute(searches: SearchDefinition*): Future[MultiSearchResponse] = execute(new MultiSearchDefinition(searches))

  def execute(req: DeleteByQueryRequest): Future[DeleteByQueryResponse] =
    injectFuture[DeleteByQueryResponse](client.deleteByQuery(req, _))
  def execute(req: DeleteByQueryDefinition): Future[DeleteByQueryResponse] = execute(req.build)

  def execute(req: CreateRepositoryDefinition): Future[PutRepositoryResponse] = {
    injectFuture[PutRepositoryResponse](client.admin.cluster.putRepository(req.build, _))
  }

  def execute(req: CreateSnapshotDefinition): Future[CreateSnapshotResponse] = {
    injectFuture[CreateSnapshotResponse](client.admin.cluster.createSnapshot(req.build, _))
  }

  def execute(opt: OptimizeRequest): Future[OptimizeResponse] = {
    injectFuture[OptimizeResponse](client.admin.indices.optimize(opt, _))
  }
  def execute(opt: OptimizeDefinition): Future[OptimizeResponse] = execute(opt.build)

  def execute(req: RestoreSnapshotDefinition): Future[RestoreSnapshotResponse] = {
    injectFuture[RestoreSnapshotResponse](client.admin.cluster.restoreSnapshot(req.build, _))
  }

  def execute(req: DeleteSnapshotDefinition): Future[DeleteSnapshotResponse] = {
    injectFuture[DeleteSnapshotResponse](client.admin.cluster.deleteSnapshot(req.build, _))
  }

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
  def execute(req: GetRequest): Future[GetResponse] = injectFuture[GetResponse](client.get(req, _))

  /** Executes a Scala DSL get and returns a scala Future with the GetResponse.
    *
    *
    * @param get a GetDefinition from the Scala DSL
    *
    * @return a Future providing an GetResponse
    */
  def execute(get: GetDefinition): Future[GetResponse] = execute(get.build)

  def execute(req: MultiGetDefinition): Future[MultiGetResponse] =
    injectFuture[MultiGetResponse](client.multiGet(req.build, _))

  def execute(c: CreateIndexDefinition): Future[CreateIndexResponse] =
    injectFuture[CreateIndexResponse](client.admin.indices.create(c.build, _))
  def execute(req: CreateIndexRequest): Future[CreateIndexResponse] =
    injectFuture[CreateIndexResponse](client.admin.indices.create(req, _))

  def execute(c: CountDefinition): Future[CountResponse] =
    injectFuture[CountResponse](client.count(c.build, _))

  def execute(i: IndexStatusDefinition): Future[IndicesStatusResponse] =
    injectFuture[IndicesStatusResponse](client.admin.indices.status(i.build, _))

  def execute(i: IndexRecoveryDefinition): Future[RecoveryResponse] =
    injectFuture[RecoveryResponse](client.admin.indices.recoveries(i.build, _))

  def execute(req: ValidateQueryRequest): Future[ValidateQueryResponse] =
    injectFuture[ValidateQueryResponse](client.admin.indices.validateQuery(req, _))

  def execute(req: UpdateRequest): Future[UpdateResponse] = injectFuture[UpdateResponse](client.update(req, _))

  def execute(req: MoreLikeThisRequest): Future[SearchResponse] =
    injectFuture[SearchResponse](client.moreLikeThis(req, _))
  def execute(req: MoreLikeThisDefinition): Future[SearchResponse] = execute(req.build)

  def execute(c: ClusterHealthDefinition): Future[ClusterHealthResponse] = {
    injectFuture[ClusterHealthResponse](client.admin.cluster.health(c.build, _))
  }

  def execute(req: ExplainDefinition): Future[ExplainResponse] = {
    injectFuture[ExplainResponse](client.explain(req.build, _))
  }

  def execute(put: PutMappingDefinition): Future[PutMappingResponse] = {
    injectFuture[PutMappingResponse](client.admin.indices.putMapping(put.build, _))
  }

  def execute(v: ValidateDefinition): Future[ValidateQueryResponse] = {
    injectFuture[ValidateQueryResponse](client.admin.indices.validateQuery(v.build, _))
  }

  def execute(p: RegisterDefinition): Future[IndexResponse] = execute(p.build)

  def execute(p: PercolateDefinition): Future[PercolateResponse] = {
    injectFuture[PercolateResponse](client.percolate(p.build, _))
  }

  def execute(req: CreateIndexTemplateDefinition): Future[PutIndexTemplateResponse] = {
    injectFuture[PutIndexTemplateResponse](client.admin.indices.putTemplate(req.build, _))
  }

  def execute(req: DeleteIndexTemplateDefinition): Future[DeleteIndexTemplateResponse] = {
    injectFuture[DeleteIndexTemplateResponse](client.admin.indices.deleteTemplate(req.build, _))
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
    injectFuture[BulkResponse](client.bulk(bulk._builder, _))
  }

  def execute(delete: DeleteIndexDefinition): Future[DeleteIndexResponse] = {
    injectFuture[DeleteIndexResponse](client.admin.indices.delete(delete.build, _))
  }

  def exists(indexes: String*): Future[IndicesExistsResponse] =
    injectFuture[IndicesExistsResponse](client.admin.indices.prepareExists(indexes: _*).execute)

  def typesExist(indices: String*)(types: String*): Future[TypesExistsResponse] =
    injectFuture[TypesExistsResponse](client.admin.indices.prepareTypesExists(indices: _*).setTypes(types: _*).execute)

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

  def execute(u: UpdateDefinition): Future[UpdateResponse] = injectFuture[UpdateResponse](client.update(u.build, _))

  def close(): Unit = client.close()

  def close(index: String): Future[CloseIndexResponse] =
    injectFuture[CloseIndexResponse](client.admin.indices.prepareClose(index).execute)

  def segments(indexes: String*): Future[IndicesSegmentResponse] =
    injectFuture[IndicesSegmentResponse](client.admin.indices.prepareSegments(indexes: _*).execute)

  def deleteMapping(indexes: String*)(types: String*) =
    injectFuture[DeleteMappingResponse](client
      .admin
      .indices
      .prepareDeleteMapping(indexes: _*)
      .setType(types: _*)
      .execute)

  def putMapping(indexes: String*)(mapping: MappingDefinition) =
    injectFuture[PutMappingResponse](client.admin.indices.preparePutMapping(indexes: _*)
      .setType(mapping.`type`).setSource(mapping.build).execute)

  def reindex(sourceIndex: String,
              targetIndex: String,
              chunkSize: Int = 500,
              scroll: String = "5m",
              preserveId: Boolean = true)(implicit ec: ExecutionContext): Future[Unit] = {
    execute {
      ElasticDsl.search in sourceIndex limit chunkSize scroll scroll searchType SearchType.Scan query matchall
    } flatMap { response =>

      def _scroll(scrollId: String): Future[Unit] = {
        searchScroll(scrollId, scroll) flatMap { response =>
          val hits = response.getHits.hits
          if (hits.length > 0) {
            Future
              .sequence(hits.map(hit => (hit.`type`, hit.getId, hit.sourceAsString)).grouped(chunkSize).map { pairs =>
              execute {
                ElasticDsl.bulk(
                  pairs map {
                    case (typ, _id, source) =>
                      val expr = index into targetIndex -> typ
                      (if (preserveId) expr id _id else expr) doc StringDocumentSource(source)
                  }: _*
                )
              }
            })
              .flatMap(_ => _scroll(response.getScrollId))
          } else {
            Future.successful(())
          }
        }
      }

      val scrollId = response.getScrollId
      _scroll(scrollId)
    }
  }

  def java = client
  def admin = client.admin

  @deprecated("Use .await() on future of async client", "1.3.0")
  def sync(implicit duration: Duration = 10.seconds) = new SyncClient(this)(duration)

  @deprecated("Use .await() on future of async client", "1.3.0")
  class SyncClient(client: ElasticClient)(implicit duration: Duration) {

    def execute(i: IndexDefinition)(implicit duration: Duration) = Await.result(client.execute(i), duration)
    def execute(c: CountDefinition)(implicit duration: Duration) = Await.result(client.execute(c), duration)
    def execute(d: DeleteIndexDefinition)(implicit duration: Duration) = Await.result(client.execute(d), duration)
    def execute(c: CreateIndexDefinition)(implicit duration: Duration) = Await.result(client.execute(c), duration)
    def execute(c: MoreLikeThisDefinition)(implicit duration: Duration) = Await.result(client.execute(c), duration)
    def execute(c: UpdateDefinition)(implicit duration: Duration) = Await.result(client.execute(c), duration)
    def execute(c: ValidateDefinition)(implicit duration: Duration) = Await.result(client.execute(c), duration)
    def execute(o: OptimizeDefinition)(implicit duration: Duration) = Await.result(client.execute(o), duration)
    def execute(get: GetDefinition)(implicit duration: Duration) = Await.result(client.execute(get), duration)

    def execute(percolateDef: PercolateDefinition)(implicit duration: Duration): PercolateResponse =
      Await.result(client.execute(percolateDef), duration)

    def execute(registerDef: RegisterDefinition)(implicit duration: Duration): IndexResponse =
      Await.result(client.execute(registerDef), duration)

    def execute(ddef: DeleteByIdDefinition)(implicit duration: Duration): DeleteResponse =
      Await.result(client.execute(ddef), duration)

    def execute(ddef: DeleteByQueryDefinition)(implicit duration: Duration): DeleteByQueryResponse =
      Await.result(client.execute(ddef), duration)

    def execute(gets: MultiGetDefinition)(implicit duration: Duration): MultiGetResponse =
      Await.result(client.execute(gets), duration)

    @deprecated("use execute", "1.3.3")
    def search(searches: SearchDefinition)(implicit duration: Duration): SearchResponse = execute(searches)

    def execute(search: SearchDefinition)(implicit duration: Duration): SearchResponse =
      Await.result(client.execute(search), duration)

    def execute(searches: SearchDefinition*)(implicit duration: Duration): MultiSearchResponse =
      Await.result(client.execute(new MultiSearchDefinition(searches)), duration)

    def execute(definition: ExplainDefinition)(implicit duration: Duration): ExplainResponse =
      Await.result(client.execute(definition), duration)

    def exists(indexes: String*): IndicesExistsResponse = Await.result(client.exists(indexes: _*), duration)

    def reindex(sourceIndex: String, targetIndex: String, chunkSize: Int = 500, scroll: String = "5m")
               (implicit ec: ExecutionContext, duration: Duration): Unit = {
      Await.result(client.reindex(sourceIndex, targetIndex, chunkSize, scroll), duration)
    }

    def execute(get: GetMappingDefinition)(implicit duration: Duration): GetMappingsResponse = {
      Await.result(client.execute(get), duration)
    }

    def execute(put: PutMappingDefinition)(implicit duration: Duration): PutMappingResponse = {
      Await.result(client.execute(put), duration)
    }

    def execute(bulk: BulkDefinition)(implicit duration: Duration): BulkResponse = {
      Await.result(client.execute(bulk), duration)
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

  def fromClient(client: Client): ElasticClient = new ElasticClient(client)
  @deprecated("timeout is no longer needed, it is ignored, so you can use the fromClient(client) method instead",
    "1.4.2")
  def fromClient(client: Client, timeout: Long): ElasticClient = fromClient(client)

  def fromNode(node: Node): ElasticClient = fromClient(node.client)
  @deprecated("timeout is no longer needed, it is ignored, so you can use the fromNode(client) method instead", "1.4.2")
  def fromNode(node: Node, timeout: Long): ElasticClient = fromNode(node)

  /** Connect this client to the single remote elasticsearch process.
    * Note: Remote means out of process, it can of course be on the local machine.
    */
  def remote(host: String, port: Int): ElasticClient = remote(ImmutableSettings.builder.build, host, port)
  def remote(uri: ElasticsearchClientUri): ElasticClient = remote(ImmutableSettings.builder.build, uri)

  def remote(settings: Settings, host: String, port: Int): ElasticClient = remote(settings, host, port)
  def remote(settings: Settings, uri: ElasticsearchClientUri): ElasticClient = {
    val client = new TransportClient(settings)
    for ( (host, port) <- uri.hosts ) client.addTransportAddress(new InetSocketTransportAddress(host, port))
    fromClient(client)
  }

  @deprecated("For multiple hosts, prefer the methods that use ElasticsearchUri", "1.4.2")
  def remote(addresses: (String, Int)*): ElasticClient = remote(ImmutableSettings.builder().build(), addresses: _*)

  @deprecated("For multiple hosts, Prefer the methods that use ElasticsearchUri", "1.4.2")
  def remote(settings: Settings, addresses: (String, Int)*): ElasticClient = {
    val client = new TransportClient(settings)
    for ( (host, port) <- addresses ) client.addTransportAddress(new InetSocketTransportAddress(host, port))
    fromClient(client)
  }

  def local: ElasticClient = local(ImmutableSettings.settingsBuilder().build())
  def local(settings: Settings): ElasticClient = {
    fromNode(NodeBuilder.nodeBuilder().local(true).data(true).settings(settings).node())
  }
  @deprecated("timeout is no longer needed, it is ignored, so you can use the local(client) method instead", "1.4.2")
  def local(settings: Settings, timeout: Long): ElasticClient = local(settings)

}

object ElasticsearchClientUri {
  private val PREFIX = "elasticsearch://"
  def apply(str: String): ElasticsearchClientUri = {
    require(str != null && str.trim.nonEmpty, "Invalid uri, must be in format elasticsearch://host:port,host:port,...")
    val withoutPrefix = str.replace(PREFIX, "")
    val hosts = withoutPrefix.split(',').map { host =>
      val parts = host.split(':')
      if (parts.size == 2) {
        parts(0) -> parts(1).toInt
      } else {
        throw new IllegalArgumentException("Invalid uri, must be in format elasticsearch://host:port,host:port,...")
      }
    }
    ElasticsearchClientUri(str, hosts.toList)
  }
}

case class ElasticsearchClientUri(uri: String, hosts: List[(String, Int)])

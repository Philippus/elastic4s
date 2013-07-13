package com.sksamuel.elastic4s

import scala.concurrent._
import org.elasticsearch.action.index.{IndexRequest, IndexResponse}
import org.elasticsearch.action.count.{CountRequest, CountResponse}
import org.elasticsearch.action.search.{MultiSearchRequest, MultiSearchResponse, SearchRequest, SearchResponse}
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
import org.elasticsearch.common.io.stream.OutputStreamStreamOutput

/** @author Stephen Samuel */
class ElasticClient(val client: org.elasticsearch.client.Client, var timeout: Long)
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
   * Indexes a Scala DSL IndexDefinition and returns a scala Future with the IndexResponse.
   *
   * @param builder an IndexDefinition from the Scala DSL
   *
   * @return a Future providing an IndexResponse
   */
  def execute(builder: IndexDefinition): Future[IndexResponse] = future {
    client.index(builder.build).actionGet(timeout)
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
   * @param builder a SearchDefinition from the Scala DSL
   *
   * @return a Future providing an SearchResponse
   */
  def execute(builder: SearchDefinition): Future[SearchResponse] = execute(builder.build)

  def result(builder: SearchDefinition)(implicit duration: Duration): SearchResponse =
    Await.result(execute(builder), duration)

  def execute(req: MultiSearchRequest): Future[MultiSearchResponse] = future {
    client.multiSearch(req).actionGet(timeout)
  }
  def execute(msearch: MultiSearchDefinition)(implicit duration: Duration): Future[MultiSearchResponse] =
    execute(msearch.build)

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
  def execute(req: GetRequest): Future[GetResponse] = future {
    client.get(req).actionGet(timeout)
  }

  /**
   * Executes a Scala DSL get and returns a scala Future with the GetResponse.
   *
   * @param builder a GetDefinition from the Scala DSL
   *
   * @return a Future providing an GetResponse
   */
  def execute(builder: GetDefinition): Future[GetResponse] = execute(builder.build)

  def execute(req: MultiGetRequest): Future[MultiGetResponse] = future {
    client.multiGet(req).actionGet(timeout)
  }
  def execute(mget: MultiGetDefinition): Future[MultiGetResponse] = execute(mget.build)

  def execute(req: DeleteRequest): Future[DeleteResponse] = future {
    client.delete(req).actionGet(timeout)
  }

  def execute(d: DeleteByIdDefinition): Future[DeleteResponse] = execute(d.builder)

  def execute(create: CreateIndexDefinition): Future[CreateIndexResponse] = future {
    create.build.writeTo(new OutputStreamStreamOutput(System.out))
    client.admin.indices.create(create.build).actionGet(timeout)
  }

  def execute(req: DeleteByQueryRequest): Future[DeleteByQueryResponse] = future {
    client.deleteByQuery(req).actionGet(timeout)
  }
  def execute(d: DeleteByQueryDefinition): Future[DeleteByQueryResponse] = execute(d.builder)

  def execute(req: ValidateQueryRequest): Future[ValidateQueryResponse] = future {
    client.admin.indices().validateQuery(req).actionGet(timeout)
  }

  def execute(validateDef: ValidateDefinition): Future[ValidateQueryResponse] = execute(validateDef.build)

  def execute(req: UpdateRequest): Future[UpdateResponse] = future {
    client.update(req).actionGet(timeout)
  }

  def execute(updateDef: UpdateDefinition): Future[UpdateResponse] = execute(updateDef.build)

  def execute(req: MoreLikeThisRequest): Future[SearchResponse] = future {
    client.moreLikeThis(req).actionGet(5000)
  }
  def execute(mltDef: MoreLikeThisDefinition): Future[SearchResponse] = execute(mltDef._builder)

  def execute(requests: BulkCompatibleRequest*): Future[BulkResponse] = {
    val bulk = client.prepareBulk()
    requests.foreach(req => req match {
      case index: IndexDefinition => bulk.add(index.build)
      case delete: DeleteByIdDefinition => bulk.add(delete.builder)
      case update: UpdateDefinition => bulk.add(update.build)
    })
    future {
      bulk.execute().actionGet(timeout)
    }
  }
  def result(requests: BulkCompatibleRequest*)(implicit duration: Duration): BulkResponse =
    Await.result(execute(requests: _*), duration)

  def exists(indexes: String*): Future[IndicesExistsResponse] = future {
    client.admin().indices().prepareExists(indexes.toSeq: _*).execute().actionGet(timeout)
  }

  def register(registerDef: RegisterDefinition): Future[IndexResponse] = execute(registerDef.build.request)

  def percolate(percolate: PercolateDefinition): Future[PercolateResponse] = future {
    client.percolate(percolate.build).actionGet(timeout)
  }

  def searchScroll(scrollId: String): Future[SearchResponse] = future {
    client.prepareSearchScroll(scrollId).execute().actionGet(timeout)
  }

  def close(): Unit = client.close()

  def java = client
  def admin = client.admin

  def sync(implicit duration: Duration = 10.seconds) = new SyncClient(this)(duration)

  class SyncClient(client: ElasticClient)(implicit duration: Duration) {

    def percolate(percolateDef: PercolateDefinition)(implicit duration: Duration): PercolateResponse =
      Await.result(client.percolate(percolateDef), duration)

    def register(registerDef: RegisterDefinition)(implicit duration: Duration): IndexResponse =
      Await.result(client.register(registerDef), duration)

    def execute(get: GetDefinition)(implicit duration: Duration): GetResponse =
      Await.result(client.execute(get), duration)

    def execute(count: CountDefinition)(implicit duration: Duration): CountResponse =
      Await.result(client.execute(count), duration)

    def execute(search: SearchDefinition)(implicit duration: Duration): SearchResponse =
      Await.result(client.execute(search), duration)

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

    def execute(mget: MultiGetDefinition)(implicit duration: Duration): MultiGetResponse =
      Await.result(client.execute(mget), duration)

    def execute(msearch: MultiSearchDefinition)(implicit duration: Duration): MultiSearchResponse =
      Await.result(client.execute(msearch), duration)

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

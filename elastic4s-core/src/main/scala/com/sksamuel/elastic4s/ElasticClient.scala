package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.StringDocumentSource
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.cluster.node.shutdown.NodesShutdownResponse
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse
import org.elasticsearch.action.admin.indices.flush.FlushResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.{ImmutableSettings, Settings}
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.{Node, NodeBuilder}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.implicitConversions

/** @author Stephen Samuel */
class ElasticClient(val client: org.elasticsearch.client.Client) extends IterableSearch {

  def execute[T, R, Q](t: T)(implicit executable: Executable[T, R, Q]): Future[Q] = executable(client, t)

  def shutdown: Future[NodesShutdownResponse] = shutdown("_local")

  def shutdown(nodeIds: String*): Future[NodesShutdownResponse] = {
    injectFuture[NodesShutdownResponse](java.admin.cluster.prepareNodesShutdown(nodeIds: _*).execute)
  }

  @deprecated("deprecated in favour of: client.execute { index exists <index> }", "1.5.5")
  def exists(indexes: String*): Future[IndicesExistsResponse] =
    injectFuture[IndicesExistsResponse](client.admin.indices.prepareExists(indexes: _*).execute)

  @deprecated("deprecated in favour of: client.execute { types exist <types> in <index>> }", "1.5.5")
  def typesExist(indices: String*)(types: String*): Future[TypesExistsResponse] =
    injectFuture[TypesExistsResponse](client.admin.indices.prepareTypesExists(indices: _*).setTypes(types: _*).execute)

  @deprecated("deprecated in favour of: client.execute { search scroll <id> }", "1.5.5")
  def searchScroll(scrollId: String) =
    injectFuture[SearchResponse](client.prepareSearchScroll(scrollId).execute)

  @deprecated("deprecated in favour of: client.execute { search scroll <id> }", "1.5.5")
  def searchScroll(scrollId: String, keepAlive: String) =
    injectFuture[SearchResponse](client.prepareSearchScroll(scrollId).setScroll(keepAlive).execute)

  @deprecated("deprecated in favour of: client.execute { flush index <index> }", "1.5.5")
  def flush(indexes: String*): Future[FlushResponse] =
    injectFuture[FlushResponse](client.admin.indices.prepareFlush(indexes: _*).execute)

  @deprecated("deprecated in favour of: client.execute { refresh index <index> }", "1.5.5")
  def refresh(indexes: String*): Future[RefreshResponse] =
    injectFuture[RefreshResponse](client.admin.indices.prepareRefresh(indexes: _*).execute)

  @deprecated("deprecated in favour of the typeclass approach; use client.execute { open index <index> }", "1.5.5")
  def open(index: String): Future[OpenIndexResponse] =
    injectFuture[OpenIndexResponse](client.admin.indices.prepareOpen(index).execute)

  def close(): Unit = client.close()

  @deprecated("deprecated in favour of the typeclass approach; use client.execute { close index <index> }", "1.5.5")
  def close(index: String): Future[CloseIndexResponse] =
    injectFuture[CloseIndexResponse](client.admin.indices.prepareClose(index).execute)

  @deprecated("deprecated in favour of the typeclass approach; use client.execute { get segments <index> }", "1.5.5")
  def segments(indexes: String*): Future[IndicesSegmentResponse] =
    injectFuture[IndicesSegmentResponse](client.admin.indices.prepareSegments(indexes: _*).execute)

  def reindex(sourceIndex: String,
              targetIndex: String,
              chunkSize: Int = 500,
              keepAlive: String = "5m",
              preserveId: Boolean = true)(implicit ec: ExecutionContext): Future[Unit] = {
    execute {
      ElasticDsl.search in sourceIndex limit chunkSize scroll keepAlive searchType SearchType.Scan query matchall
    } flatMap { response =>

      def _scroll(scrollId: String): Future[Unit] = {
        execute {
          search scroll scrollId keepAlive keepAlive
        } flatMap { response =>
          val hits = response.getHits.hits
          if (hits.nonEmpty) {
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

  protected def injectFuture[R](f: ActionListener[R] => Unit): Future[R] = {
    val p = Promise[R]()
    f(new ActionListener[R] {
      def onFailure(e: Throwable): Unit = p.tryFailure(e)
      def onResponse(response: R): Unit = p.trySuccess(response)
    })
    p.future
  }

  def java = client
  def admin = client.admin

  @deprecated("Use .await() on future of async client", "1.3.0")
  def sync(implicit duration: Duration = 10.seconds) = new SyncClient(this)(duration)

  override def iterateSearch(query: SearchDefinition)(implicit timeout: Duration): Iterator[SearchResponse] = {
    IterableSearch(this).iterateSearch(query)
  }
}

object ElasticClient {

  def fromClient(client: Client): ElasticClient = new ElasticClient(client)
  @deprecated("timeout is no longer needed, it is ignored, use the fromClient(client) method instead", "1.4.2")
  def fromClient(client: Client, timeout: Long): ElasticClient = fromClient(client)

  def fromNode(node: Node): ElasticClient = fromClient(node.client)
  @deprecated("timeout is no longer needed, it is ignored, use the fromNode(client) method instead", "1.4.2")
  def fromNode(node: Node, timeout: Long): ElasticClient = fromNode(node)

  /** Connect this client to the single remote elasticsearch process.
    * Note: Remote means out of process, it can of course be on the local machine.
    */
  def remote(host: String, port: Int): ElasticClient = remote(ImmutableSettings.builder.build, host, port)
  def remote(settings: Settings, host: String, port: Int): ElasticClient = {
    val client = new TransportClient(settings)
    client.addTransportAddress(new InetSocketTransportAddress(host, port))
    fromClient(client)
  }

  def remote(uri: ElasticsearchClientUri): ElasticClient = remote(ImmutableSettings.builder.build, uri)
  def remote(settings: Settings, uri: ElasticsearchClientUri): ElasticClient = {
    val client = new TransportClient(settings)
    for ( (host, port) <- uri.hosts ) client.addTransportAddress(new InetSocketTransportAddress(host, port))
    fromClient(client)
  }

  @deprecated("For multiple hosts, prefer the methods that use ElasticsearchClientUri", "1.4.2")
  def remote(addresses: (String, Int)*): ElasticClient = remote(ImmutableSettings.builder().build(), addresses: _*)

  @deprecated("For multiple hosts, Prefer the methods that use ElasticsearchClientUri", "1.4.2")
  def remote(settings: Settings, addresses: (String, Int)*): ElasticClient = {
    val client = new TransportClient(settings)
    for ( (host, port) <- addresses ) client.addTransportAddress(new InetSocketTransportAddress(host, port))
    fromClient(client)
  }

  def data : ElasticClient = data(ImmutableSettings.builder.build)
  def data(settings: Settings): ElasticClient = fromNode(NodeBuilder.nodeBuilder().data(true).settings(settings).node())

  def local: ElasticClient = local(ImmutableSettings.settingsBuilder().build())
  def local(settings: Settings): ElasticClient = {
    fromNode(NodeBuilder.nodeBuilder().local(true).data(true).settings(settings).node())
  }
  @deprecated("timeout is no longer needed, it is ignored, so you can use the local(client) method instead", "1.4.2")
  def local(settings: Settings, timeout: Long): ElasticClient = local(settings)
}

object ElasticsearchClientUri {
  private val PREFIX = "elasticsearch://"
  implicit def stringtoUri(str: String): ElasticsearchClientUri = ElasticsearchClientUri(str)
  def apply(str: String): ElasticsearchClientUri = {
    require(str != null && str.trim.nonEmpty, "Invalid uri, must be in format elasticsearch://host:port,host:port,...")
    val withoutPrefix = str.replace(PREFIX, "")
    val hosts = withoutPrefix.split(',').map { host =>
      val parts = host.split(':')
      if (parts.length == 2) {
        parts(0) -> parts(1).toInt
      } else {
        throw new IllegalArgumentException("Invalid uri, must be in format elasticsearch://host:port,host:port,...")
      }
    }
    ElasticsearchClientUri(str, hosts.toList)
  }
}

case class ElasticsearchClientUri(uri: String, hosts: List[(String, Int)])

package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.StringDocumentSource
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.client.{AdminClient, Client}
import org.elasticsearch.common.settings.{ImmutableSettings, Settings}
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.{Node, NodeBuilder}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.implicitConversions

/** @author Stephen Samuel */
class ElasticClient(val client: org.elasticsearch.client.Client) extends IterableSearch {

  def execute[T, R, Q](t: T)(implicit executable: Executable[T, R, Q]): Future[Q] = executable(client, t)

  def close(): Unit = client.close()

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

  def java: Client = client
  def admin: AdminClient = client.admin

  override def iterateSearch(query: SearchDefinition)(implicit timeout: Duration): Iterator[SearchResponse] = {
    IterableSearch(this).iterateSearch(query)
  }
}

object ElasticClient {

  def fromClient(client: Client): ElasticClient = new ElasticClient(client)
  def fromNode(node: Node): ElasticClient = fromClient(node.client)

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

  def data: ElasticClient = data(ImmutableSettings.builder.build)
  def data(settings: Settings): ElasticClient = fromNode(NodeBuilder.nodeBuilder().data(true).settings(settings).node())

  def local: ElasticClient = local(ImmutableSettings.settingsBuilder().build())
  def local(settings: Settings): ElasticClient = {
    fromNode(NodeBuilder.nodeBuilder().local(true).data(true).settings(settings).node())
  }
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

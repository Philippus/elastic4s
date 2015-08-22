package com.sksamuel.elastic4s

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.client.{AdminClient, Client}
import org.elasticsearch.common.settings.{ImmutableSettings, Settings}
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.{Node, NodeBuilder}

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.implicitConversions
import scala.util.Try

/** @author Stephen Samuel */
class ElasticClient(val client: org.elasticsearch.client.Client,
                    node: Option[Node] = None) extends IterableSearch {

  def execute[T, R, Q](t: T)(implicit executable: Executable[T, R, Q]): Future[Q] = executable(client, t)

  def close(): Unit = {
    Try {
      client.close()
    }
    Try {
      node.foreach(_.close)
    }
  }

  def java: Client = client
  def admin: AdminClient = client.admin

  override def iterateSearch(query: SearchDefinition)(implicit timeout: Duration): Iterator[SearchResponse] = {
    IterableSearch(this).iterateSearch(query)
  }
}

object ElasticClient {

  /**
   * Creates an ElasticClient which wraps an existing Client.
   *
   * Note: If you use this method, then calling close on the client instance will not shutdown
   * any local node(s). Those must be managed by the caller of this method.
   *
   * @param client the client to wrap
   */
  def fromClient(client: Client): ElasticClient = new ElasticClient(client)

  /**
   * Creates an ElasticClient by requesting a client from a given Node.
   *
   * Note: This method will not manage the lifecycle of the node. Calling close on the client
   * will shutdown only the transport mechansim between the client and the node.
   *
   * @param node the node a client will connect to
   */
  def fromNode(node: Node): ElasticClient = new ElasticClient(node.client)

  @deprecated("use the remote method with an instance of ElasticsearchClientUri or uri format string", "2.0.0")
  def remote(host: String, port: Int): ElasticClient = remote(ImmutableSettings.builder.build, host, port)

  @deprecated("use the remote method with an instance of ElasticsearchClientUri or uri format string", "2.0.0")
  def remote(settings: Settings, host: String, port: Int): ElasticClient = {
    remote(settings, ElasticsearchClientUri(host, port))
  }

  /**
   * Creates an ElasticClient connected to the elasticsearch instance(s) specified by the uri.
   * This method will use default settings.
   *
   * Note: The method name 'remote' refers to the fact that the client will connect to the instance(s)
   * using the transport client rather than becoming a full node itself and joining the cluster.
   * This is what most people think of when they talk about a client, like you would in mongo or mysql for example.
   *
   * @param uri the instance(s) to connect to.
   */
  def remote(uri: ElasticsearchClientUri): ElasticClient = remote(ImmutableSettings.builder.build, uri)

  /**
   * Connects to elasticsearch instance(s) specified by the uri and setting the
   * given settings object on the client.
   *
   * Note: The method name 'remote' refers to the fact that the client will connect to the instance(s)
   * using the transport client rather than becoming a full node itself and joining the cluster.
   * This is what most people think of when they talk about a client, like you would in mongo or mysql for example.
   *
   * @param settings the settings as applicable to the client.
   * @param uri the instance(s) to connect to.
   */
  def remote(settings: Settings, uri: ElasticsearchClientUri): ElasticClient = {
    val client = new TransportClient(settings)
    for ( (host, port) <- uri.hosts ) client.addTransportAddress(new InetSocketTransportAddress(host, port))
    fromClient(client)
  }

  /**
   * Creates a local data node. This is useful for embedded usage, or for unit tests.
   * Default settings will be applied.
   */
  def local: ElasticClient = local(ImmutableSettings.settingsBuilder().build())

  /**
   * Creates a local data node. This is useful for embedded usage, or for unit tests.
   * @param settings the settings object to set on the node
   */
  def local(settings: Settings): ElasticClient = {
    val node = NodeBuilder.nodeBuilder().local(true).data(true).settings(settings).node()
    new ElasticClient(node.client, Some(node))
  }
}

object ElasticsearchClientUri {

  private val PREFIX = "elasticsearch://"

  implicit def stringtoUri(str: String): ElasticsearchClientUri = ElasticsearchClientUri(str)

  def apply(host: String, port: Int): ElasticsearchClientUri = apply(s"elasticsearch://$host:$port")

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

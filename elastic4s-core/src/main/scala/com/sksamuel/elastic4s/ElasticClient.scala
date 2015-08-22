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

/** @author Stephen Samuel */
class ElasticClient(val client: org.elasticsearch.client.Client) extends IterableSearch {

  def execute[T, R, Q](t: T)(implicit executable: Executable[T, R, Q]): Future[Q] = executable(client, t)

  def close(): Unit = client.close()

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

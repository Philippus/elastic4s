package com.sksamuel.elastic4s

import java.net.InetSocketAddress

import cats.Show
import com.sksamuel.exts.Logging
import org.elasticsearch.{ElasticsearchException, ElasticsearchWrapperException}
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.node.Node
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.transport.client.PreBuiltTransportClient

import scala.concurrent.Future

trait TcpClient {

  def close(): Unit

  def java: Client

  // returns a String containing the Json of the request
  def show[T](request: T)(implicit show: Show[T]): String = show.show(request)

  def execute[T, R, Q](request: T)(implicit executable: Executable[T, R, Q]): Future[Q] = {
    try {
      executable(java, request)
    } catch {
      case e: ElasticsearchException => Future.failed(e)
      case e: ElasticsearchWrapperException => Future.failed(e)
    }
  }
}

@deprecated("ElasticClient is now TcpClient", "5.2.0")
trait ElasticClient extends TcpClient

trait TcpClientConstructors extends Logging {

    /**
    * Creates an ElasticClient which wraps an existing Client.
    *
    * @param client the client to wrap
    */
  def fromClient(client: Client): ElasticClient = new ElasticClient {
    def close(): Unit = client.close()
    def java: Client = client
  }

  /**
    * Creates an ElasticClient by requesting a client from a given Node.
    *
    * @param node the node a client will connect to
    */
  def fromNode(node: Node): ElasticClient = new ElasticClient {
    private val client = node.client()
    def close(): Unit = client.close()
    def java: Client = client
  }

  /**
    * Creates an ElasticClient connected to the elasticsearch instance(s) specified by the uri.
    * This method will use settings from the URI string and default plugins.
    *
    * The created client will use the standard plugins provided by the PreBuiltTransportClient instance.
    *
    * @param uri the instance(s) to connect to.
    */
  @deprecated("use transport", "5.0.0")
  def remote(uri: ElasticsearchClientUri): ElasticClient = transport(uri)
  def transport(uri: ElasticsearchClientUri): ElasticClient = transport(Settings.EMPTY, uri)

  /**
    * Creates an ElasticClient connected to the elasticsearch instance(s) specified by the uri.
    *
    * Any options set on the URI will be added to the given settings object before the client is created.
    * If a setting is specified in both the settings object and the uri, the version in the supplied
    * settings object will be used.
    *
    * Any given plugins will be added to the client in addition to the standard plugins provided
    * by the PreBuiltTransportClient instance.
    *
    * @param settings the settings as applicable to the client.
    * @param uri      the instance(s) to connect to.
    * @param plugins  the plugins to add to the client.
    */
  def transport(settings: Settings,
                uri: ElasticsearchClientUri,
                plugins: Class[_ <: Plugin]*): ElasticClient = {

    val combinedSettings = uri.options.foldLeft(Settings.builder().put(settings)) { (builder, kv) =>
      if (builder.get(kv._1) == null)
        builder.put(kv._1, kv._2)
      builder
    }.build()

    if (!combinedSettings.getAsMap.containsKey("cluster.name")) {
      logger.warn(
        """No cluster.name was specified in the settings for the client." +
        "This will still work if your cluster has the default name, but it is recommended you always set the cluster.name to avoid issues""")
    }

    val client = new PreBuiltTransportClient(combinedSettings, plugins: _*)
    for ((host, port) <- uri.hosts) {
      client.addTransportAddress(new TransportAddress(new InetSocketAddress(host, port)))
    }
    fromClient(client)
  }
}

object TcpClient extends TcpClientConstructors

@deprecated("use the equivalent methods on TcpClient", "5.2.0")
object ElasticClient extends TcpClientConstructors

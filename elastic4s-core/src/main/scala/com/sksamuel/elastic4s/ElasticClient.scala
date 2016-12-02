package com.sksamuel.elastic4s

import java.net.InetSocketAddress

import com.sksamuel.exts.{Logging, StringOption}
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.Node
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient
import org.elasticsearch.{ElasticsearchException, ElasticsearchWrapperException}

import scala.concurrent._
import scala.language.implicitConversions

trait ElasticClient {

  def execute[T, R, Q](t: T)(implicit executable: Executable[T, R, Q]): Future[Q] = {
    try {
      executable(java, t)
    } catch {
      case e: ElasticsearchException => Future.failed(e)
      case e: ElasticsearchWrapperException => Future.failed(e)
    }
  }

  def close(): Unit

  // return the underlying Java API client
  def java: Client
}

object ElasticClient extends Logging {

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
  def transport(uri: ElasticsearchClientUri): ElasticClient = transport(Settings.EMPTY, uri, false)

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
   * @param uri the instance(s) to connect to.
   * @param plugins the plugins to add to the client.
   */
  @deprecated("use transport", "5.0.0")
  def remote(settings: Settings, uri: ElasticsearchClientUri): ElasticClient = transport(settings, uri, false)
  def transport(settings: Settings, uri: ElasticsearchClientUri, plugins: Class[_ <: Plugin]*): ElasticClient = transport(settings, uri, false, plugins:_*)

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
    * @param uri the instance(s) to connect to.
    * @param enableXPack enable the XPack transport client for a secured elasticsearch cluster
    * @param plugins the plugins to add to the client.
    */
  def transport(settings: Settings,
                uri: ElasticsearchClientUri,
                enableXPack: Boolean,
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

    val client = if (enableXPack) {
      new PreBuiltXPackTransportClient(combinedSettings, plugins: _*)
    } else {
      new PreBuiltTransportClient(combinedSettings, plugins: _*)
    }

    for ( (host, port) <- uri.hosts ) {
      client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host, port)))
    }

    fromClient(client)
  }
}

object ElasticsearchClientUri {

  private val Regex = "elasticsearch://(.*?)(\\?.*?)?".r

  implicit def stringtoUri(str: String): ElasticsearchClientUri = ElasticsearchClientUri(str)

  /**
   * Creates an ElasticsearchClientUri from a single host and port with no options.
   */
  def apply(host: String, port: Int): ElasticsearchClientUri = apply(s"elasticsearch://$host:$port")

  def apply(str: String): ElasticsearchClientUri = {
    str match {
      case Regex(hoststr, query) =>
        val hosts = hoststr.split(',').map(_.split(':')).map {
          case Array(host, port) => (host, port.toInt)
          case _ => sys.error(s"Invalid hosts/ports $hoststr")
        }
        val options = StringOption(query)
          .map(_.drop(1))
          .map(_.split('&')).getOrElse(Array.empty)
          .map(_.split('=')).collect {
          case Array(key, value) => (key, value)
          case _ => sys.error(s"Invalid query $query")
        }
        ElasticsearchClientUri(str, hosts.toList, options.toMap)
      case _ => sys.error("Invalid uri, must be in format elasticsearch://host:port,host:port?querystr")
    }
  }
}

/**
* Uri used to connect to an Elasticsearch cluster. The general format is
*
* elasticsearch://host:port,host:port?querystring
*
* Multiple host:port combinations can be specified, seperated by commas.
* Options can be specified using standard uri query string syntax, eg cluster.name=superman
*/
case class ElasticsearchClientUri(uri: String, hosts: List[(String, Int)], options: Map[String, String] = Map.empty)

package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.exts.Logging
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node
import org.elasticsearch.node.internal.InternalSettingsPreparer
import org.elasticsearch.percolator.PercolatorPlugin
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.script.mustache.MustachePlugin
import org.elasticsearch.transport.Netty3Plugin

import scala.collection.JavaConverters._

class InternalNode(settings: Settings,
                   plugins: List[Class[_ <: Plugin]])
  extends Node(InternalSettingsPreparer.prepareEnvironment(settings, null), plugins.asJava)

class LocalNode(settings: Settings) extends Logging {

  private val plugins = List(classOf[Netty3Plugin], classOf[MustachePlugin], classOf[PercolatorPlugin])
  private val node = new InternalNode(settings, plugins)

  def start(): String = {
    node.start()

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        logger.info(s"Shutting down local node ${settings.get("cluster.name")}")
        LocalNode.this.stop()
      }
    })

    val nodeId = node.client().admin().cluster().prepareState().get().getState().getNodes().getLocalNodeId()
    val ipAndPort = node.client().admin().cluster().prepareNodesInfo(nodeId).get().getNodes().iterator().next()
      .getHttp().address().publishAddress().toString()
    ipAndPort
  }

  def stop() = node.close()

  /**
   * Returns an ElasticClient connected to this node.
   *
   * If shutdownNodeOnClose is true, then will shutdown this node once the client is closed, otherwise
   * you are required to manage the lifecycle of the local node yourself.
   */
  def client(shutdownNodeOnClose: Boolean = true): ElasticClient = new ElasticClient {

    private val client = {
      node.start()
      node.client()
    }

    override def close(): Unit = {
      client.close()
      if (shutdownNodeOnClose)
        node.close()
    }

    override def java: Client = client
  }
}

object LocalNode {

  def apply(clusterName: String, pathHome: String, pathData: String): LocalNode = {
    val map = Map(
      "path.home" -> pathHome,
      "path.data" -> pathData,
      "transport.type" -> "local",
      "discovery.type" -> "local",
      "http.type" -> "netty3",
      "node.ingest" -> "true",
      "script.inline" -> "true",
      "script.stored" -> "true",
      "cluster.name" -> clusterName
    )
    val settings = map.foldLeft(Settings.builder) { (settings, kv) => settings.put(kv._1, kv._2) }.build()
    new LocalNode(settings)
  }
}

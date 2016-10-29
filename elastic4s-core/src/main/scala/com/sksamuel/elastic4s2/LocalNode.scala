package com.sksamuel.elastic4s2

import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node
import org.elasticsearch.node.internal.InternalSettingsPreparer
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.transport.Netty3Plugin

import scala.collection.JavaConverters._

class LocalNode(settings: Settings) {

  class InternalNode(settings: Settings,
                     plugins: List[Class[_ <: Plugin]])
    extends Node(InternalSettingsPreparer.prepareEnvironment(settings, null), plugins.asJava)

  val plugins = List(classOf[Netty3Plugin])
  val node = new InternalNode(settings, plugins)

  def start(): String = {
    node.start()
    val localNodeId = node.client().admin().cluster().prepareState().get().getState().getNodes().getLocalNodeId()
    node.client().admin().cluster().prepareNodesInfo(localNodeId).get().getNodes().iterator().next().getHttp().address()
      .publishAddress().toString()
  }

  def stop() = node.close()

  /**
   * Returns an ElasticClient connected to this node.
   *
   * If shutdownNodeOnClose is true, then will shutdown this node once the client is closed, otherwise
   * you are required to manage the lifecycle of the local node yourself.
   */
  def client(shutdownNodeOnClose: Boolean = true): ElasticClient = new ElasticClient {

    private val client = node.client()

    override def close(): Unit = {
      client.close()
      if (shutdownNodeOnClose)
        node.close()
    }

    override def java: Client = client
  }
}

object LocalNode {
  def apply(clusterName: String, pathHome: String): LocalNode = {
    val map = Map(
      "path.home" -> pathHome,
      "transport.type" -> "local",
      "discovery.type" -> "local",
      "node.ingest" -> "true",
      "script.inline" -> "true",
      "cluster.name" -> clusterName
    )
    val settings = map.foldLeft(Settings.builder) { (settings, kv) => settings.put(kv._1, kv._2) }.build()
    new LocalNode(settings)
  }
}

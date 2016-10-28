package com.sksamuel.elastic4s

import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node
import org.elasticsearch.node.internal.InternalSettingsPreparer
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.transport.Netty3Plugin

import scala.collection.JavaConverters._

class LocalNode(clusterName: String, pathHome: String) {

  class InternalNode(settings: Settings,
                     plugins: List[Class[_ <: Plugin]])
    extends Node(InternalSettingsPreparer.prepareEnvironment(settings, null), plugins.asJava)

  val map = Map(
    "path.home" -> pathHome,
    "transport.type" -> "local",
    "discovery.type" -> "local",
    "node.ingest" -> "true",
    "script.inline" -> "true",
    "cluster.name" -> clusterName
  )

  val settings = map.foldLeft(Settings.builder) { (settings, kv) => settings.put(kv._1, kv._2) }.build()
  val plugins = List(classOf[Netty3Plugin])
  val node = new InternalNode(settings, plugins)

  def start(): String = {
    node.start()
    val localNodeId = node.client().admin().cluster().prepareState().get().getState().getNodes().getLocalNodeId()
    node.client().admin().cluster().prepareNodesInfo(localNodeId).get().getNodes().iterator().next().getHttp().address()
      .publishAddress().toString()
  }

  def stop() = node.close()

  def client(): ElasticClient = ElasticClient.fromNode(node)
}

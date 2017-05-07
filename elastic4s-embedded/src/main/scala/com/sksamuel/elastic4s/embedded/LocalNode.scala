package com.sksamuel.elastic4s.embedded

import java.io.File
import java.nio.file.{Path, Paths}

import com.sksamuel.elastic4s.TcpClient
import com.sksamuel.exts.Logging
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.index.reindex.ReindexPlugin
import org.elasticsearch.node.{InternalSettingsPreparer, Node}
import org.elasticsearch.percolator.PercolatorPlugin
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.script.mustache.MustachePlugin
import org.elasticsearch.transport.Netty4Plugin

import scala.collection.JavaConverters._
import scala.util.Try

class LocalNode(settings: Settings, plugins: List[Class[_ <: Plugin]])
  extends Node(InternalSettingsPreparer.prepareEnvironment(settings, null), plugins.asJava) with Logging {
  super.start()

  logger.info("Registering shutdown hook for local node")
  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = {
      logger.info(s"Shutting down local node ${settings.get("cluster.name")}")
      LocalNode.this.stop()
    }
  })


  val nodeId: String = client().admin().cluster().prepareState().get().getState.getNodes.getLocalNodeId

  private val nodeinfo = client().admin().cluster().prepareNodesInfo(nodeId).get().getNodes.iterator().next()

  // the ip and port might not be available via http if http is disabled, in this case we'll set them to 0
  val ipAndPort: String = Option(nodeinfo.getHttp).map(_.address.publishAddress.toString).getOrElse("localhost:-1")
  logger.info(s"LocalNode started @ $ipAndPort")
  logger.info(s"LocalNode data location ${settings.get("path.data")}")

  val ip: String = ipAndPort.takeWhile(_ != ':')
  val port: Int = ipAndPort.dropWhile(_ != ':').drop(1).toInt

  def stop(removeData: Boolean = false): Any = {
    super.close()

    def deleteDir(dir: File): Unit = {
      dir.listFiles().foreach {
        case file if file.isFile => file.delete()
        case file if file.isDirectory => deleteDir(file)
      }
      dir.delete()
    }

    if (removeData) {
      Try { deleteDir(pathData.toAbsolutePath.toFile) }
      Try { deleteDir(pathRepo.toAbsolutePath.toFile) }
      Try { deleteDir(pathHome.toAbsolutePath.toFile) }
    }
  }

  // the path that is used for the "path.home" property of the elasticsearch node
  val pathHome: Path = Paths get settings.get("path.home")

  // the path that is used for the "path.data" property of the elasticsearch node
  val pathData: Path = Paths get settings.get("path.data")

  // the path that is used for the "path.repo" property of the elasticsearch node
  val pathRepo: Path = Paths get settings.get("path.repo")

  // the location of the config folder for this node
  val pathConfig: Path = pathHome resolve "config"

  /**
  * Returns a new ElasticClient connected to this node.
  *
  * If shutdownNodeOnClose is true, then the local node will be shutdown once this
  * client is closed. Otherwise you are required to manage the lifecycle of the local node yourself.
  */
  def elastic4sclient(shutdownNodeOnClose: Boolean = true): TcpClient =
    new LocalElasticClient(this, shutdownNodeOnClose)
}

class LocalElasticClient(node: LocalNode, shutdownNodeOnClose: Boolean) extends TcpClient {

  override val java: Client = {
    node.start()
    node.client()
  }

  override def close(): Unit = {
    java.close()
    if (shutdownNodeOnClose)
      node.stop()
  }
}

object LocalNode {

  // creates a LocalNode with all settings provided by the user
  // and using default plugins
  def apply(settings: Settings): LocalNode = {
    require(settings.getAsMap.containsKey("cluster.name"))
    require(settings.getAsMap.containsKey("path.home"))
    require(settings.getAsMap.containsKey("path.data"))
    require(settings.getAsMap.containsKey("path.repo"))

    val plugins = List(classOf[Netty4Plugin], classOf[MustachePlugin], classOf[PercolatorPlugin], classOf[ReindexPlugin])

    val mergedSettings = Settings.builder().put(settings)
      .put("transport.type", "local")
      .put("http.type", "netty4")
      .put("http.enabled", "true")
      .build()

    new LocalNode(mergedSettings, plugins)
  }

  // creates a LocalNode with all settings provided by the user
  def apply(settings: Map[String, String]): LocalNode = apply(Settings.builder().put(settings.asJava).build)

  // returns the minimum required settings to create a local node
  def requiredSettings(clusterName: String, homePath: String): Map[String, String] = {
    Map(
      "cluster.name" -> clusterName,
      "path.home" -> homePath,
      "path.repo" -> Paths.get(homePath).resolve("repo").toString,
      "path.data" -> Paths.get(homePath).resolve("data").toString
    )
  }

  /**
  *   Creates a new LocalNode with default settings using the given cluster name and home path.
  *   Other required directories are created inside the path home folder.
  *
  *   By using this method, the following settings are enabled:
  *
  *   "node.ingest" -> "true"
  *   "script.inline" -> "true"
  *   "script.stored" -> "true"
  *   "http.enabled" -> "true"
  */
  def apply(clusterName: String, pathHome: String): LocalNode = apply(requiredSettings(clusterName, pathHome))
}

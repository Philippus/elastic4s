package com.sksamuel.elastic4s.embedded

import java.io.File
import java.nio.file.{Path, Paths}

import com.sksamuel.elastic4s.http.{ElasticClient, HttpClient}
import com.sksamuel.exts.Logging
import org.elasticsearch.analysis.common.CommonAnalysisPlugin
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.index.reindex.ReindexPlugin
import org.elasticsearch.node.{InternalSettingsPreparer, Node}
import org.elasticsearch.percolator.PercolatorPlugin
import org.elasticsearch.plugins.Plugin
import org.elasticsearch.script.mustache.MustachePlugin
import org.elasticsearch.transport.Netty4Plugin

import scala.util.Try

trait LocalNode {
  def nodeId: String
  def ip: String
  def host: String
  def port: Int
  def client(shutdownNodeOnClose: Boolean): ElasticClient
  def clusterName: String
  def pathData: Path
  def pathHome: Path

  // the location of the config folder for this node
  def pathConfig: Path = pathHome resolve "config"
}

// a connection to a local node that was already started
class RemoteLocalNode(val clusterName: String,
                      val nodeId: String,
                      val ip: String,
                      httpAddress: String,
                      transportAddress: String,
                      val pathData: Path,
                      val pathHome: Path,
                      pathRepo: Path)
    extends LocalNode {
  require(httpAddress != null, "httpAddress cannot be null")
  require(transportAddress != null, "transportAddress cannot be null")

  override def client(shutdownNodeOnClose: Boolean): ElasticClient =
    ElasticClient(s"elasticsearch://$host:$port?cluster.name=$clusterName")
  override def host: String = httpAddress.split(':').head
  override def port: Int    = httpAddress.split(':').last.toInt
}

import scala.collection.JavaConverters._

// a new locally started internal node
class InternalLocalNode(settings: Settings, plugins: List[Class[_ <: Plugin]])
  extends Node(InternalSettingsPreparer.prepareEnvironment(settings, null), plugins.asJavaCollection, false)
    with LocalNode
    with Logging {
  super.start()

  logger.info("Registering shutdown hook for local node")
  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = {
      logger.info(s"Shutting down local node ${settings.get("cluster.name")}")
      InternalLocalNode.this.stop()
    }
  })

  override val nodeId: String = client().admin().cluster().prepareState().get().getState.getNodes.getLocalNodeId

  private val nodeinfo = client().admin().cluster().prepareNodesInfo(nodeId).get().getNodes.iterator().next()

  // the ip and port might not be available via http if http is disabled, in this case we'll set them to 0
  val ipAndPort: String = Option(nodeinfo.getHttp).map(_.address.publishAddress.toString).getOrElse("localhost:-1")
  logger.info(s"LocalNode started @ $ipAndPort")
  logger.info(s"LocalNode data location ${settings.get("path.data")}")

  override val ip: String   = ipAndPort.takeWhile(_ != ':')
  override val host: String = ip
  override val port: Int    = ipAndPort.dropWhile(_ != ':').drop(1).toInt

  def stop(removeData: Boolean = false): Any = {
    super.close()

    def deleteDir(dir: File): Unit = {
      dir.listFiles().foreach {
        case file if file.isFile      => file.delete()
        case file if file.isDirectory => deleteDir(file)
      }
      dir.delete()
    }

    if (removeData) {
      Try {
        deleteDir(pathData.toAbsolutePath.toFile)
      }
      Try {
        deleteDir(pathRepo.toAbsolutePath.toFile)
      }
      Try {
        deleteDir(pathHome.toAbsolutePath.toFile)
      }
    }
  }

  // the path that is used for the "path.home" property of the elasticsearch node
  override val pathHome: Path = Paths get settings.get("path.home")

  // the path that is used for the "path.data" property of the elasticsearch node
  override val pathData: Path = Paths get settings.get("path.data")

  // the path that is used for the "path.repo" property of the elasticsearch node
  val pathRepo: Path = Paths get settings.get("path.repo")

  override val clusterName: String = settings.get("cluster.name")

  /**
    * Returns a new HttpClient connected to this node.
    *
    * If shutdownNodeOnClose is true, then the local node will be shutdown once this
    * client is closed. Otherwise you are required to manage the lifecycle of the local node yourself.
    */
  override def client(shutdownNodeOnClose: Boolean): ElasticClient = new ElasticClient {
    private val delegate            = ElasticClient(s"elasticsearch://$host:$port")
    override def client: HttpClient = delegate.client
    override def close(): Unit = {
      Try {
        delegate.client.close()
      }
      if (shutdownNodeOnClose)
        stop()
    }
  }
  override def registerDerivedNodeNameWithLogger(nodeName: String): Unit = ()
}

object LocalNode {

  // creates a LocalNode with all settings provided by the user
  // and using default plugins
  def apply(settings: Settings): InternalLocalNode = {
    require(settings.get("cluster.name") != null)
    require(settings.get("path.home") != null)
    require(settings.get("path.data") != null)
    require(settings.get("path.repo") != null)

    val plugins =
      List(classOf[Netty4Plugin], classOf[MustachePlugin], classOf[PercolatorPlugin], classOf[ReindexPlugin], classOf[CommonAnalysisPlugin])

    val mergedSettings = Settings
      .builder()
      .put(settings)
      .put("http.type", "netty4")
      .put("http.enabled", "true")
      .put("node.max_local_storage_nodes", "10")
      .build()

    new InternalLocalNode(mergedSettings, plugins)
  }

  // creates a LocalNode with all settings provided by the user
  def apply(map: Map[String, String]): InternalLocalNode = {
    val settings = map.foldLeft(Settings.builder) { case (builder, (key, value)) => builder.put(key, value) }.build()
    apply(settings)
  }

  // returns the minimum required settings to create a local node
  def requiredSettings(clusterName: String, homePath: String): Map[String, String] =
    Map(
      "cluster.name"                 -> clusterName,
      "path.home"                    -> homePath,
      "node.max_local_storage_nodes" -> "10",
      "path.repo"                    -> Paths.get(homePath).resolve("repo").toString,
      "path.data"                    -> Paths.get(homePath).resolve("data").toString
    )

  /**
    * Creates a new LocalNode with default settings using the given cluster name and home path.
    * Other required directories are created inside the path home folder.
    */
  def apply(clusterName: String, pathHome: String): InternalLocalNode = apply(requiredSettings(clusterName, pathHome))
}

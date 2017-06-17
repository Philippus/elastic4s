package com.sksamuel.elastic4s.testkit

import java.nio.file.{Path, Paths}
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

import com.sksamuel.elastic4s.TcpClient
import com.sksamuel.elastic4s.embedded.{LocalNode, RemoteLocalNode}
import com.sksamuel.elastic4s.http.HttpClient

import scala.util.Random
import scala.util.control.NonFatal

// LocalNodeProvider provides helper methods to create a local (embedded) node
trait LocalNodeProvider {
  // returns an embedded, started, node
  def getNode: LocalNode
  def node: LocalNode = getNode

  implicit lazy val client: TcpClient = getNode.tcp(false)
  implicit lazy val http: HttpClient = getNode.http(false)
}

// implementation of LocalNodeProvider that uses a single
// node instance for all classes in the same thread classloader.
trait ClassloaderLocalNodeProvider extends LocalNodeProvider {
  override def getNode: LocalNode = ClassloaderLocalNode.node
}

// implementation of LocalNodeProvider that attempts to find local nodes already started
// and then connects to that, or creates a new one if one cannot be found
trait DiscoveryLocalNodeProvider extends LocalNodeProvider {
  override def getNode: LocalNode = {

    try {
      // assume the local node is running on 9200
      val client = HttpClient("elasticsearch://localhost:9200")
      import com.sksamuel.elastic4s.http.ElasticDsl._
      val nodeinfo = client.execute(nodeInfo()).await
      val (id, node) = nodeinfo.nodes.head
      println(s"Found local node $id")
      new RemoteLocalNode(nodeinfo.clusterName, id, node.ip, node.httpAddress, node.transportAddress)

    } catch {
      case NonFatal(e) =>
        println(s"Creating new local node")
        val tempDirectoryPath: Path = Paths get System.getProperty("java.io.tmpdir")
        val pathHome: Path = tempDirectoryPath resolve UUID.randomUUID().toString
        LocalNode("localnode-cluster", pathHome.toAbsolutePath.toString)
    }
  }
}

// implementation of LocalNodeProvider that uses a single
// node instance for each class that mixes in this trait.
trait ClassLocalNodeProvider extends LocalNodeProvider {

  private lazy val tempDirectoryPath: Path = Paths get System.getProperty("java.io.tmpdir")
  private lazy val pathHome: Path = tempDirectoryPath resolve UUID.randomUUID().toString

  override lazy val getNode = LocalNode(
    "node_" + ClassLocalNodeProvider.counter.getAndIncrement(),
    pathHome.toAbsolutePath.toString
  )
}

object ClassLocalNodeProvider {
  val counter = new AtomicLong(1)
}

trait AlwaysNewLocalNodeProvider extends LocalNodeProvider {

  private def tempDirectoryPath: Path = Paths get System.getProperty("java.io.tmpdir")
  private def pathHome: Path = tempDirectoryPath resolve UUID.randomUUID().toString

  override def getNode: LocalNode = {
    LocalNode(
      "node_" + Random.nextInt(),
      pathHome.toAbsolutePath.toString
    )
  }
}

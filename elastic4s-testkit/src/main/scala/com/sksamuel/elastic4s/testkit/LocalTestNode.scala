package com.sksamuel.elastic4s.testkit

import java.nio.file.{Path, Paths}
import java.util.UUID

import com.sksamuel.elastic4s.ElasticClient

import scala.concurrent.duration._

object SingleTestNode {

  lazy val tempDirectoryPath: Path = Paths get System.getProperty("java.io.tmpdir")
  lazy val testNodeHomePath: Path = tempDirectoryPath resolve UUID.randomUUID().toString
  lazy val testNodeDataPath: Path = tempDirectoryPath resolve UUID.randomUUID().toString

  lazy val node: LocalNode = {
    val node = LocalNode(
      getClass.getSimpleName,
      testNodeHomePath.toAbsolutePath.toString,
      testNodeDataPath.toAbsolutePath.toString
    )
    node.start()
    node
  }

  lazy val client = node.client(true)
}

trait SingleTestNode extends LocalTestNode {
  override def createLocalClient: ElasticClient = SingleTestNode.client
}

trait LocalTestNode {

  lazy val tempDirectoryPath: Path = Paths get System.getProperty("java.io.tmpdir")

  /**
   * Override this if you wish to change where the home directory for the local instance will be located.
   */
  lazy val testNodeHomePath: Path = tempDirectoryPath resolve UUID.randomUUID().toString
  lazy val testNodeDataPath: Path = tempDirectoryPath resolve UUID.randomUUID().toString

  def numberOfReplicas: Int = 0

  def numberOfShards: Int = 1

  def indexRefresh: FiniteDuration = 1.seconds

  def httpEnabled: Boolean = true

  lazy val testNodeConfPath: Path = {
    val path = testNodeHomePath resolve "config"
    path.toFile.mkdirs()
    path
  }

  /**
   * Is invoked when a test needs access to a client for the test node.
   * Can override this if you wish to control precisely how the client is created.
   */
  implicit def client: ElasticClient = internalClient
  private lazy val internalClient = createLocalClient

  /**
   * Invoked to create a local client for the elastic node.
   * Override to create the client youself.
   */
  def createLocalClient: ElasticClient = {
    val node = LocalNode(
      getClass.getSimpleName,
      testNodeHomePath.toAbsolutePath.toString,
      testNodeDataPath.toAbsolutePath.toString
    )
    node.start()
    node.client(true)
  }
}

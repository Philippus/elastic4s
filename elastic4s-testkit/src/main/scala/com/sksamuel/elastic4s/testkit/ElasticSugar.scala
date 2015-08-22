package com.sksamuel.elastic4s.testkit

import java.io.PrintWriter
import java.nio.file.{Path, Paths}
import java.util.UUID

import com.sksamuel.elastic4s.{ElasticDsl, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus
import org.elasticsearch.common.settings.ImmutableSettings
import org.slf4j.LoggerFactory
import scala.concurrent.duration._

/** @author Stephen Samuel */
trait ElasticNodeBuilder {

  private val logger = LoggerFactory.getLogger(getClass)

  /**
   * Override this if you wish to change where the home directory for the local instance will be located.
   */
  lazy val testNodeHomePath: Path = tempDirectoryPath resolve UUID.randomUUID().toString

  def numberOfReplicas: Int = 0

  def numberOfShards: Int = 1

  def disableDynamicScripting: Boolean = false

  def indexRefresh: FiniteDuration = 1.seconds

  def httpEnabled: Boolean = true

  lazy val tempDirectoryPath: Path = Paths get System.getProperty("java.io.tmpdir")

  lazy val testNodeConfPath: Path = testNodeHomePath resolve "config"

  /**
   * Override this if you wish to control all the settings used by the client.
   */
  protected def settings: ImmutableSettings.Builder = {

    val home = testNodeHomePath
    logger.info(s"Elasticsearch test-server located at $home")
    home.toFile.mkdirs()
    home.toFile.deleteOnExit()

    val conf = testNodeConfPath
    conf.toFile.mkdirs()
    conf.toFile.deleteOnExit()

    // todo this needs to come out of here and into the analyzer test alone when we can isolate nodes
    val newStopListFile = (testNodeConfPath resolve "stoplist.txt").toFile
    val writer = new PrintWriter(newStopListFile)
    writer.write("a\nan\nthe\nis\nand\nwhich") // writing the stop words to the file
    writer.close()

    val builder = ImmutableSettings.settingsBuilder()
      .put("node.http.enabled", httpEnabled)
      .put("http.enabled", httpEnabled)
      .put("path.home", home.toFile.getAbsolutePath)
      .put("path.repo", home.toFile.getAbsolutePath)
      .put("path.conf", conf.toFile.getAbsolutePath)
      .put("index.number_of_shards", numberOfShards)
      .put("index.number_of_replicas", numberOfReplicas)
      .put("script.disable_dynamic", disableDynamicScripting)
      .put("index.refresh_interval", indexRefresh.toSeconds + "s")
      .put("discovery.zen.ping.multicast.enabled", "false")
      .put("es.logger.level", "INFO")
      .put("cluster.name", getClass.getSimpleName)
    configureSettings(builder)
  }

  /**
   * Invoked by the sugar trait to setup the settings builder that was created by settings()
   */
  def configureSettings(builder: ImmutableSettings.Builder): ImmutableSettings.Builder = builder

  /**
   * Invoked to create a local client for the elastic node.
   * Override to create the client youself.
   */
  def createLocalClient: ElasticClient = ElasticClient.local(settings.build)
}

trait ElasticSugar extends ElasticNodeBuilder {

  private val logger = LoggerFactory.getLogger(getClass)

  private lazy val internalClient = createLocalClient

  /**
   * Is invoked when a test needs access to a client for the test node.
   * Can override this if you wish to control precisely how the client is created.
   */
  implicit def client: ElasticClient = internalClient

  def refresh(indexes: String*) {
    val i = indexes.size match {
      case 0 => Seq("_all")
      case _ => indexes
    }
    client.execute {
      ElasticDsl.refresh index indexes
    }
  }

  def blockUntilGreen(): Unit = {
    blockUntil("Expected cluster to have green status") { () =>
      client.execute {
        get cluster health
      }.await.getStatus == ClusterHealthStatus.GREEN
    }
  }

  def blockUntil(explain: String)(predicate: () => Boolean): Unit = {

    var backoff = 0
    var done = false

    while (backoff <= 16 && !done) {
      if (backoff > 0) Thread.sleep(200 * backoff)
      backoff = backoff + 1
      try {
        done = predicate()
      } catch {
        case e: Throwable => logger.warn("problem while testing predicate", e)
      }
    }

    require(done, s"Failed waiting on: $explain")
  }

  def ensureIndexExists(index: String): Unit = {
    val resp = client.execute {
      indexExists(index)
    }.await
    if (!resp.isExists)
      client.execute {
        create index index
      }.await
  }

  def blockUntilDocumentExists(id: String, index: String, `type`: String): Unit = {
    blockUntil(s"Expected to find document $id") {
      () =>
        client.execute {
          get id id from index / `type`
        }.await.isExists
    }
  }

  /**
   * Will block until the given index and optional types have at least the given number of documents.
   */
  def blockUntilCount(expected: Long, index: String, types: String*): Unit = {
    blockUntil(s"Expected count of $expected") {
      () =>
        expected <= client.execute {
          count from index types types
        }.await.getCount
    }
  }

  def blockUntilExactCount(expected: Long, index: String, types: String*): Unit = {
    blockUntil(s"Expected count of $expected") {
      () =>
        expected == client.execute {
          count from index types types
        }.await.getCount
    }
  }

  def blockUntilEmpty(index: String): Unit = {
    blockUntil(s"Expected empty index $index") {
      () =>
        client.execute {
          count from index
        }.await.getCount == 0
    }
  }
  def blockUntilIndexExists(index: String): Unit = {
    blockUntil(s"Expected exists index $index") {
      () ⇒
        client.execute {
          indexExists(index)
        }.await.isExists
    }
  }

  def blockUntilIndexNotExists(index: String): Unit = {
    blockUntil(s"Expected not exists index $index") {
      () ⇒
        !client.execute {
          indexExists(index)
        }.await.isExists
    }
  }

  def blockUntilDocumentHasVersion(index: String, `type`: String, id: String, version: Long): Unit = {
    blockUntil(s"Expected document $id to have version $version") {
      () =>
        client.execute {
          get id id from index -> `type`
        }.await.getVersion == version
    }
  }
}

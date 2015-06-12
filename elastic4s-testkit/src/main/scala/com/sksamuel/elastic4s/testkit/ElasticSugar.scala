package com.sksamuel.elastic4s.testkit

import java.io.File
import java.util.UUID

import com.sksamuel.elastic4s.{ElasticDsl, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.common.settings.ImmutableSettings
import org.slf4j.LoggerFactory

/** @author Stephen Samuel */

object TestElasticNode {

  private val logger = LoggerFactory.getLogger(getClass)

  val tempFile = File.createTempFile("elasticsearchtests", "tmp")
  val homeDir = new File(tempFile.getParent + "/" + UUID.randomUUID().toString)
  logger.info(s"Elasticsearch test-server located at $homeDir")

  homeDir.mkdir()
  homeDir.deleteOnExit()
  tempFile.deleteOnExit()

  val settings = ImmutableSettings.settingsBuilder()
    .put("node.http.enabled", false)
    .put("http.enabled", false)
    .put("path.home", homeDir.getAbsolutePath)
    .put("index.number_of_shards", 1)
    .put("index.number_of_replicas", 0)
    .put("script.disable_dynamic", false)
    .put("index.refresh_interval", "1s")
    //.put("indices.memory.index_buffer_size", "20%")
    //.put("index.translog.flush_threshold_size", "500mb")
    //.put("index.store.throttle.max_bytes_per_sec", "500mb")
    .put("es.logger.level", "INFO")

  implicit lazy val client = ElasticClient.local(settings.build)
}

trait ElasticSugar {

  private val logger = LoggerFactory.getLogger(getClass)

  val client = TestElasticNode.client

  def refresh(indexes: String*) {
    val i = indexes.size match {
      case 0 => Seq("_all")
      case _ => indexes
    }
    client.execute {
      ElasticDsl.refresh index indexes
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

  def blockUntilCount(expected: Long, index: String, types: String*): Unit = {
    blockUntil(s"Expected count of $expected") { () =>
      val actual = client.execute {
        count from index types types
      }.await.getCount
      expected <= actual
    }
  }

  def blockUntilEmpty(index: String): Unit = {
    blockUntil(s"Expected empty index $index") { () =>
      val actual = client.execute {
        count from index
      }.await.getCount
      actual == 0
    }
  }
}

package com.sksamuel.elastic4s

import java.io.File
import java.util.UUID

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.indices.IndexMissingException
import org.scalatest.{BeforeAndAfterAll, Suite}

/** @author Stephen Samuel */

object TestElasticNode extends Logging {

  val tempFile = File.createTempFile("elasticsearchtests", "tmp")
  val homeDir = new File(tempFile.getParent + "/" + UUID.randomUUID().toString)
  homeDir.mkdir()
  homeDir.deleteOnExit()
  tempFile.deleteOnExit()
  logger.info("Setting ES home dir [{}]", homeDir)

  val settings = ImmutableSettings.settingsBuilder()
    .put("node.http.enabled", false)
    .put("http.enabled", false)
    .put("path.home", homeDir.getAbsolutePath)
    .put("index.number_of_shards", 1)
    .put("index.number_of_replicas", 0)
    .put("script.disable_dynamic", false)
    //.put("indices.ttl.interval", "60s")
    //.put("indices.memory.index_buffer_size", "20%")
    //.put("index.translog.flush_threshold_size", "500mb")
    //.put("index.store.throttle.max_bytes_per_sec", "500mb")
    .put("es.logger.level", "INFO")

  implicit val client = ElasticClient.local(settings.build)
}

trait ElasticSugar extends BeforeAndAfterAll with Logging {

  this: Suite =>

  val client = TestElasticNode.client

  def refresh(indexes: String*) {
    val i = indexes.size match {
      case 0 => Seq("_all")
      case _ => indexes
    }
    val listener = client.client.admin().indices().prepareRefresh(i: _*).execute()
    listener.actionGet()
  }

  def blockUntil(explain: String)(predicate: () ⇒ Boolean): Unit = {
    var backoff = 0
    var done = false

    while (backoff <= 500 && !done) {
      if (backoff > 0) Thread.sleep(10000)
      backoff = backoff + 1
      try {
        done = predicate()
      } catch {
        case e: Throwable ⇒ logger.warn("problem while testing predicate", e)
      }
    }

    require(done, s"Failed waiting on: $explain")
  }

  def blockUntilCount(expected: Long, index: String, types: String*): Unit =
    blockUntil(s"Expected count of $expected") { () ⇒
      val actual = client.execute {
        count from index types types
      }.await.getCount
      expected <= actual
    }
}

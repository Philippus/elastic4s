package com.sksamuel.elastic4s

import java.io.File
import java.util.UUID

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.indices.IndexMissingException
import org.scalatest.{ BeforeAndAfterAll, Suite }

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
    .put("es.logger.level", "DEBUG")

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

  def blockUntilCount(expected: Long,
                      index: String,
                      types: String*) {

    var backoff = 0
    var actual = 0l

    while (backoff <= 50 && actual != expected) {
      if (backoff > 0)
        Thread.sleep(100)
      backoff = backoff + 1
      try {
        actual = client.execute {
          count from index types types
        }.await.getCount
      } catch {
        case e: IndexMissingException => 0
      }
    }

    require(expected == actual, s"Block failed waiting on count: Expected was $expected but actual was $actual")
  }
}

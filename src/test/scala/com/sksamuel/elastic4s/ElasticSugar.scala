package com.sksamuel.elastic4s

import org.elasticsearch.common.settings.ImmutableSettings
import java.io.File
import ElasticDsl._
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration._
import org.elasticsearch.indices.IndexMissingException
import org.scalatest.{ Suite, BeforeAndAfterAll }

/** @author Stephen Samuel */
trait ElasticSugar extends BeforeAndAfterAll with Logging {

  this: Suite =>

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

  implicit val client = ElasticClient.local(settings.build)

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

    while (backoff <= 128 && actual != expected) {
      if (backoff > 0)
        Thread.sleep(backoff * 100)
      backoff = if (backoff == 0) 1 else backoff * 2
      try {
        actual = Await.result(client execute {
          count from index types types
        }, 5 seconds).getCount
      } catch {
        case e: IndexMissingException => 0
      }
    }

    require(expected == actual, s"Block failed waiting on count: Expected was $expected but actual was $actual")
  }
}

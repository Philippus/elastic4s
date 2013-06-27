package com.sksamuel.elastic4s

import org.elasticsearch.common.settings.ImmutableSettings
import java.io.File
import CountDsl._
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration._

/** @author Stephen Samuel */
trait ElasticSugar extends Logging {

    val tempDir = File.createTempFile("elasticsearchtests", "tmp").getParent
    val dataDir = new File(tempDir + "/" + UUID.randomUUID().toString)
    dataDir.mkdir()
    dataDir.deleteOnExit()
    logger.info("Setting ES data dir [{}]", dataDir)

    val settings = ImmutableSettings.settingsBuilder()
      .put("node.http.enabled", false)
      .put("http.enabled", false)
      .put("path.data", dataDir.getAbsolutePath)
      .put("index.number_of_shards", 1)
      .put("index.number_of_replicas", 0)

    implicit val client = ElasticClient.local(settings.build)

    def refresh(indexes: String*) {
        val i = indexes.size match {
            case 0 => Seq("*")
            case _ => indexes
        }
        client.client.admin().indices().prepareRefresh(i: _*).setWaitForOperations(true).execute()
    }

    def blockUntil(expectedCount: Long,
                   index: String,
                   `type`: String) {

        val query = count from index types `type`
        var backoff = 1
        var actualCount = Await.result(client.execute(query), 5 seconds).getCount

        while (backoff <= 64 && actualCount != expectedCount) {
            Thread.sleep(backoff * 100)
            backoff = backoff * 2
            actualCount = Await.result(client.execute(query), 5 seconds).getCount
        }
    }
}

package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class SearchTest extends FlatSpec with MockitoSugar with ElasticSugar {

    client.execute {
        index into "music/bands" fields (
          "name" -> "coldplay",
          "singer" -> "chris martin",
          "drummer" -> "will champion",
          "guitar" -> "johnny buckland"
          )
    }
    client.execute {
        index into "music/artists" fields (
          "name" -> "kate bush",
          "singer" -> "kate bush"
          )
    }
    client.execute {
        index into "music/bands" fields (
          "name" -> "jethro tull",
          "singer" -> "ian anderson",
          "guitar" -> "martin barre",
          "keyboards" -> "johnny smith"
          ) id 45
    }

    client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

    refresh("music")
    blockUntilCount(3, "music")

    client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

    "a search index" should "find an indexed document that matches a string query" in {

        val resp = client.sync.execute {
            search in "music" types "bands" query "coldplay"
        }
        assert(1 === resp.getHits.totalHits())
    }

    "a search index" should "return the correct count for a count with query" in {

        val resp = client.sync.execute {
            count from "music" query "johnny buckland"
        }
        assert(2 === resp.getCount)
    }
}

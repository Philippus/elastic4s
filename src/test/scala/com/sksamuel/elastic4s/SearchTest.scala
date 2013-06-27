package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.IndexDsl._
import SearchDsl._
import scala.concurrent.Await
import scala.concurrent.duration._

/** @author Stephen Samuel */
class SearchTest extends FlatSpec with MockitoSugar with ElasticSugar {

    val resp = client.execute {
        index into "music/bands" fields (
          "name" -> "coldplay",
          "singer" -> "chris martin",
          "drummer" -> "will champion"
          )
    }
    Await.ready(resp, 10 seconds)
    refresh("music")
    blockUntil(1, "music", "bands")

    "a search index" should "find an indexed document that matches a query" in {

        val future = client execute {
            search in "music" types "bands" query "coldplay"
        }
        val resp = Await.result(future, 10 seconds)
        assert(1 === resp.getHits.totalHits())
    }
}

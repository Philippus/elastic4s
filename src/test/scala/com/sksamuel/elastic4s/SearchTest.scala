package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.IndexDsl._
import SearchDsl._
import CountDsl._
import GetDsl._
import scala.concurrent.Await
import scala.concurrent.duration._

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
    refresh("music")
    blockUntilCount(3, "music")

    "a search index" should "find an indexed document that matches a string query" in {

        val future = client execute {
            search in "music" types "bands" query "coldplay"
        }
        val resp = Await.result(future, 10 seconds)
        assert(1 === resp.getHits.totalHits())
    }

    "a search index" should "return the correct count for a count with query" in {

        val future = client execute {
            count from "music" query "johnny buckland"
        }
        val resp = Await.result(future, 10 seconds)
        assert(2 === resp.getCount)
    }

    "a search index" should "retrieve a document by id" in {

        val future = client execute {
            get id 45 from "music/band"
        }
        val resp = Await.result(future, 10 seconds)
        assert("45" === resp.getId)
    }
}

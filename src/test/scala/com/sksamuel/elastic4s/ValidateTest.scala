package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.IndexDsl._
import ValidateDsl._
import scala.concurrent.Await
import scala.concurrent.duration._

/** @author Stephen Samuel */
class ValidateTest extends FlatSpec with MockitoSugar with ElasticSugar {

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
    refresh("music")
    blockUntilCount(2, "music")

    "a validate query" should "return valid when the query is valid" in {

        val future = client execute {
            validate in "music/bands" query "coldplay"
        }
        val resp = Await.result(future, 10 seconds)
        assert(true === resp.isValid)
    }
}

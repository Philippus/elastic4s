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
        index into "food/pasta" fields (
          "name" -> "maccaroni",
          "color" -> "yellow"
          )
    }
    refresh("food")
    blockUntilCount(1, "food")

    "a validate query" should "return valid when the query is valid" in {

        val future = client execute {
            validate in "food/pasta" query "maccaroni"
        }
        val resp = Await.result(future, 10 seconds)
        assert(true === resp.isValid)
    }
}

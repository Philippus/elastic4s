package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

import scala.concurrent.duration._

/** @author Stephen Samuel */
class ValidateTest extends FlatSpec with MockitoSugar with ElasticSugar {

  implicit val duration: Duration = 10.seconds

  client.execute {
    index into "food/pasta" fields (
      "name" -> "maccaroni",
      "color" -> "yellow"
    )
  }.await

  blockUntilCount(1, "food")

  "a validate query" should "return valid when the query is valid" in {

    val resp = client.execute {
      validate in "food/pasta" query "maccaroni"
    }.await
    assert(true === resp.isValid)
  }
}

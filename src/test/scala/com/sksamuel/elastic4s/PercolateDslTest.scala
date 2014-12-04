package com.sksamuel.elastic4s

import org.scalatest.{ FlatSpec, OneInstancePerTest }
import org.scalatest.mock.MockitoSugar
import ElasticDsl._

/** @author Stephen Samuel */
class PercolateDslTest extends FlatSpec with MockitoSugar with JsonSugar with OneInstancePerTest {
  "the percolate dsl" should "should generate json for a register query" in {
    val req = register id 2 into "captains" query termQuery("name", "cook") fields { "color" -> "blue" }
    req.build.source.toUtf8 should matchJsonResource("/json/percolate/percolate_register.json")
  }

  it should "should generate fields json for a percolate request" in {
    val req = percolate in "captains" doc "name" -> "cook" query { termQuery("color" -> "blue") }
    req._doc.string should matchJsonResource("/json/percolate/percolate_request.json")
  }

  it should "should use raw doc for a percolate request" in {
    val req = percolate in "captains" rawDoc { """{ "name": "cook" }""" } query { termQuery("color" -> "blue") }
    req._doc.string should matchJsonResource("/json/percolate/percolate_request.json")
  }
}

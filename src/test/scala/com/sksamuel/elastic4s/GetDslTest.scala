package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._

/** @author Stephen Samuel */
class GetDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "a get by id request" should "accept tuple for from" in {
    val req = get id 123 from "places" -> "cities"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "accept two parameters" in {
    val req = get id 123 from("places", "cities")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "parse slash indextype" in {
    val req = get id 123 from "places/cities"
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }
}

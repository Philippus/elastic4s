package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._

/** @author Stephen Samuel */
class ExplainDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "an explain request" should "accept tuple for index type" in {
    val req = explain id 123 in "places" -> "cities" query regex("name", "col.pla.")
    assert(req.build.id() === "123")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "accept two parameters for index / type" in {
    val req = explain id 123 in ("places", "cities") query termQuery("name", "sammy")
    assert(req.build.id() === "123")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "parse slash indextype" in {
    val req = explain id 123 in "places/cities" query "coldplay"
    assert(req.build.id() === "123")
    assert(req.build.index() === "places")
    assert(req.build.`type`() === "cities")
  }

  it should "support preference" in {
    val req = explain id 123 in "places/cities" preference "mypref"
    assert(req.build.preference() === "mypref")
  }

  it should "support routing" in {
    val req = explain id 123 in "places/cities" routing "route66"
    assert(req.build.routing() === "route66")
  }
}

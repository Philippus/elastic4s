package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._

/** @author Stephen Samuel */
class ValidateDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "a validate request" should "accept tuple for index type" in {
    val req = validate in "places" -> "cities" query regexQuery("name", "col.pla.")
    assert(req.build.indices() === Array("places"))
  }

  it should "accept two parameters for index / type" in {
    val req = validate in ("places", "cities") query termQuery("name", "sammy")
    assert(req.build.indices() === Array("places"))
  }

  it should "parse slash indextype" in {
    val req = validate in "places/cities" query stringQuery("coldplay")
    assert(req.build.indices() === Array("places"))
  }
}

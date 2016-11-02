package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

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

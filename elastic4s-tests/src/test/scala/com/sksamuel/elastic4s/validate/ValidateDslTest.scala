package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.FlatSpec
import org.scalatest.mockito.MockitoSugar

class ValidateDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "a validate request" should "accept tuple for index type" in {
    val req = validateIn("places" -> "cities") query regexQuery("name", "col.pla.")
  }

  it should "accept two parameters for index / type" in {
    val req = validateIn(("places", "cities")) query termQuery("name", "sammy")
  }

  it should "parse slash indextype" in {
    val req = validateIn("places/cities") query stringQuery("coldplay")
  }
}

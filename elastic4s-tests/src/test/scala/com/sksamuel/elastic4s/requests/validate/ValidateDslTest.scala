package com.sksamuel.elastic4s.requests.validate

import org.scalatest.FlatSpec
import org.scalatestplus.mockito.MockitoSugar

class ValidateDslTest extends FlatSpec with MockitoSugar {

  import com.sksamuel.elastic4s.ElasticDsl._

  "a validate request" should "accept tuple for index type" in {
    validateIn("places") query regexQuery("name", "col.pla.")
  }

  it should "accept two parameters for index / type" in {
    validateIn("places") query termQuery("name", "sammy")
  }

  it should "parse slash indextype" in {
    validateIn("places/cities") query stringQuery("coldplay")
  }
}

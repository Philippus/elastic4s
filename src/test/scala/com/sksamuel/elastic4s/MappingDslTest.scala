package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

/** @author Stephen Samuel */
class MappingDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  import ElasticDsl._

  "the mapping dsl" should "accept a get mapping request" in {
    client.execute {
      mapping("index")
    }
  }

  it should "accept a get mapping request in infix form" in {
    client.execute {
      mapping from "index"
    }
  }
}

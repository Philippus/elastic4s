package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._

/** @author Stephen Samuel */
class DeleteIndexDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "a delete index request" should "accept var args" in {
    val req = delete index("index1", "index2")
    assert(req.build.indices() === Array("index1", "index2"))
  }

  it should "accept single index as a postfix" in {
    val req = delete index "places"
    assert(req.build.indices() === Array("places"))
  }
}

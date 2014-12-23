package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

class RecoveryDslTest extends FlatSpec with MockitoSugar with ElasticSugar {
  "an index recovery request" should "accept a single index" in {
    val req = recover index "index-0"
    assert(req.build.indices() === Array("index-0"))
  }

  "an index recovery request" should "accept multiple indices" in {
    val req = recover index("index-1", "index-2")
    assert(req.build.indices() === Array("index-1", "index-2"))
  }
}


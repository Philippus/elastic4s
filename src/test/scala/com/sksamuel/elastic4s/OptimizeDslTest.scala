package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.ElasticDsl._

/** @author Stephen Samuel */
class OptimizeDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "an optimize request" should "accept var args" in {
    val opt = optimize("index1", "index2").maxSegments(5)
    assert(opt.build.indices() === Array("index1", "index2"))
    assert(opt.build.maxNumSegments() === 5)
  }

  it should "accept single index version" in {
    val opt = optimize index "index1" flush true
    assert(opt.build.indices() === Array("index1"))
    assert(opt.build.flush())
  }
}
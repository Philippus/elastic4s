package com.sksamuel.elastic4s

import org.scalatest.FunSuite
import ElasticDsl._

/** @author Stephen Samuel */
class ClientDslTest extends FunSuite with ElasticSugar {

  test("sync compiles with mapping from") {
    client.execute {
      mapping from "index"
    }
  }

  test("async compiles with mapping from") {
    client.sync.execute {
      mapping from "index"
    }
  }
}

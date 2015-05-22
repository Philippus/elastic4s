package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl
import org.scalatest.FunSuite

class FlushIndexDslTest extends FunSuite with ElasticDsl {

  test("flush index dsl compiles") {
    flush index "myindex"
  }
}

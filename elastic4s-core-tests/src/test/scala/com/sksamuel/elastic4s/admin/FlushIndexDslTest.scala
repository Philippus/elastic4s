package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl2$
import org.scalatest.FunSuite

class FlushIndexDslTest extends FunSuite with ElasticDsl2 {

  test("flush index dsl compiles") {
    flush index "myindex"
  }
}

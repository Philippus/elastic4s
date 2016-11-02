package com.sksamuel.elastic4s2.admin

import com.sksamuel.elastic4s2.ElasticDsl2$
import org.scalatest.FunSuite

class FlushIndexDslTest extends FunSuite with ElasticDsl2 {

  test("flush index dsl compiles") {
    flush index "myindex"
  }
}

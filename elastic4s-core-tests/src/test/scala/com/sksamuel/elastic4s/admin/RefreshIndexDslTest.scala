package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl2$
import org.scalatest.FunSuite

class RefreshIndexDslTest extends FunSuite with ElasticDsl2 {

  test("refresh index dsl compiles") {
    refresh index "myindex"
  }
}

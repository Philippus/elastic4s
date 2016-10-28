package com.sksamuel.elastic4s2.admin

import com.sksamuel.elastic4s2.ElasticDsl2$
import org.scalatest.FunSuite

class RefreshIndexDslTest extends FunSuite with ElasticDsl2 {

  test("refresh index dsl compiles") {
    refresh index "myindex"
  }
}

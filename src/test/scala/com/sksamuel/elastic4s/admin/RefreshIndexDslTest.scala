package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl
import org.scalatest.FunSuite

class RefreshIndexDslTest extends FunSuite with ElasticDsl {

  test("refresh index dsl compiles") {
    refresh index "myindex"
  }
}

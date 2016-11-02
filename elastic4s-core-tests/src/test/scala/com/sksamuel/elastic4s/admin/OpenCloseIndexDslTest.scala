package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl2$
import org.scalatest.FunSuite

class OpenCloseIndexDslTest extends FunSuite with ElasticDsl2 {

  test("open index dsl compiles") {
    open index "myindex"
  }

  test("close index dsl compiles") {
    close index "myindex"
  }
}

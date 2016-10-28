package com.sksamuel.elastic4s2.admin

import com.sksamuel.elastic4s2.ElasticDsl2$
import org.scalatest.FunSuite

class SegmentsDslTest extends FunSuite with ElasticDsl2 {

  test("segments dsl compiles") {
    get segments "myindex"
  }
}

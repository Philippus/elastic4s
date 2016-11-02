package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl2$
import org.scalatest.FunSuite

class SegmentsDslTest extends FunSuite with ElasticDsl2 {

  test("segments dsl compiles") {
    get segments "myindex"
  }
}

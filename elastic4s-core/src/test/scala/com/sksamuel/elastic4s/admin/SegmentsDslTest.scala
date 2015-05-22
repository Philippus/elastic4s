package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl
import org.scalatest.FunSuite

class SegmentsDslTest extends FunSuite with ElasticDsl {

  test("segments dsl compiles") {
    get segments "myindex"
  }
}

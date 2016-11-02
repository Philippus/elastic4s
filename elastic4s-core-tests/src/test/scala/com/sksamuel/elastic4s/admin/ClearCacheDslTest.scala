package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl2$
import org.scalatest.FunSuite

class ClearCacheDslTest extends FunSuite with ElasticDsl2 {

  test("clear cache dsl compiles") {
    clear cache "myindex"
  }
}

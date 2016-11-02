package com.sksamuel.elastic4s2.admin

import com.sksamuel.elastic4s2.ElasticDsl2$
import org.scalatest.FunSuite

class ClearCacheDslTest extends FunSuite with ElasticDsl2 {

  test("clear cache dsl compiles") {
    clear cache "myindex"
  }
}

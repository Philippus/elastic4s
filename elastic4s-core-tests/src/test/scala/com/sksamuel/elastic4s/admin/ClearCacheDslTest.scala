package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl
import org.scalatest.FunSuite

class ClearCacheDslTest extends FunSuite with ElasticDsl {

  test("clear cache dsl compiles") {
    clear cache "myindex"
  }
}

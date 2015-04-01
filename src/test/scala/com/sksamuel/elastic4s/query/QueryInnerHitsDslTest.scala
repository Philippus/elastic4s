package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.ElasticDsl
import org.scalatest.WordSpec

class QueryInnerHitsDslTest extends WordSpec with ElasticDsl {

  "query inner hits" should {
    "compile" in {
      search in "index" / "type" query {
        nestedQuery("somepath") inner {
          inner hits "name" from 2 size 10
        }
      }
    }
  }
}

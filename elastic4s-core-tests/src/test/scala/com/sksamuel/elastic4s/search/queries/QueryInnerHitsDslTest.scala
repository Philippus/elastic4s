package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.ElasticDsl2$
import org.scalatest.WordSpec

class QueryInnerHitsDslTest extends WordSpec with ElasticDsl2 {

  "query inner hits" should {
    "compile" in {
      search in "index" / "type" query {
        nestedQuery("somepath") query "qweqwe" inner {
          inner hits "name" from 2 size 10 highlighting (
              highlight field "x" matchedFields "x" order "score" fragmentSize 18 numberOfFragments 5
            ) sourceInclude "x"
        }
      }
    }
  }
}

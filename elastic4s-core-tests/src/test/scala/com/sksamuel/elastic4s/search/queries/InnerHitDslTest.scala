package com.sksamuel.elastic4s2.search.queries

import com.sksamuel.elastic4s2.ElasticDsl2$
import org.scalatest.WordSpec

class InnerHitDslTest extends WordSpec with ElasticDsl2 {

  "top level inner hits" should {
    "compile" in {
      search in "index" / "type" inner (
        inner hit "name" path "path",
        inner hit "name" `type` "type"
      )
    }
  }
}

package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl2$
import org.scalatest.WordSpec
import com.sksamuel.elastic4s.testkit.ElasticSugar

class TypeExistsTest extends WordSpec with ElasticSugar with ElasticDsl2 {

  client.execute {
    index into "typeexiststest" / "quantumleap" fields "name" -> "sam"
  }.await

  "type exists" should {
    "return true for existing type" in {

      val resp = client.execute {
        types exist "quantumleap" in "typeexiststest"
      }.await

      assert(resp.isExists === true)
    }
    "return false for non existing type in existing index" in {

      val resp = client.execute {
        types exist "qwewqe" in "typeexiststest"
      }.await

      assert(resp.isExists === false)
    }
  }
}

package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.{ ElasticDsl, ElasticSugar }
import org.scalatest.WordSpec

class TypeExistsTest extends WordSpec with ElasticSugar with ElasticDsl {

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

package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.sksamuel.elastic4s.{ WhitespaceAnalyzer, ElasticDsl }
import org.scalatest.WordSpec
import com.sksamuel.elastic4s.testkit.ElasticSugar

class IndexExistsTest extends WordSpec with ElasticSugar with ElasticDsl {

  client.execute {
    create index "indexexiststest" mappings {
      "r" as Seq(
        field name "a" withType StringType stored true analyzer WhitespaceAnalyzer,
        field name "b" withType StringType
      )
    }
  }.await

  "index exists" should {
    "return true for existing index" in {

      val resp = client.execute {
        index exists "indexexiststest"
      }.await

      assert(resp.isExists === true)
    }
    "return false for non existing index" in {

      val resp = client.execute {
        index exists "qweqwewqe"
      }.await

      assert(resp.isExists === false)
    }
  }
}

package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.index.mappings.IndexMappings
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FunSuite, Matchers}

import scala.util.Try

class PutMappingHttpTest extends FunSuite with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {

  test("put mapping should add new field to an existing type") {

    Try {
      http.execute {
        deleteIndex("putmaptest")
      }.await
    }

    http.execute {
      createIndex("putmaptest").mappings(
        mapping("a").fields(
          keywordField("foo")
        )
      )
    }.await

    http.execute {
      putMapping("putmaptest" / "a").fields(
        keywordField("moo")
      )
    }.await

    http.execute {
      getMapping("putmaptest" / "a")
    }.await shouldBe Seq(IndexMappings("putmaptest", Map("a" -> Map("foo" -> Map("type" -> "keyword"), "moo" -> Map("type" -> "keyword")))))
  }

  test("put mapping should support raw source") {

    val raw = """{"properties":{"createdAt":{"type":"date","index":"not_analyzed","doc_values":true,"format":"yyyy-MM-dd HH:mm:ss ZZ"}},"_all":{"enabled":false},"numeric_detection":false,"dynamic_date_formats":["yyyy-MM-dd","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss ZZ","yyyy-MM-dd'T'HH:mm:ss.SSSZZ"],"dynamic_templates":[{"not_analyzed_template":{"match":"*","match_mapping_type":"string","mapping":{"type":"string","index":"not_analyzed","doc_values":true}}},{"long_template":{"match":"*","match_mapping_type":"long","mapping":{"type":"long","doc_values":true}}},{"date_template":{"match":"*","match_mapping_type":"date","mapping":{"type":"date","doc_values":true,"format":"yyyy-MM-dd HH:mm:ss ZZ||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss.SSSZZ||yyyy-MM-dd"}}},{"double_template":{"match":"*","match_mapping_type":"double","mapping":{"type":"double","doc_values":true}}}]}"""

    Try {
      http.execute {
        deleteIndex("putrawtest")
      }.await
    }

    http.execute {
      createIndex("putrawtest").mappings(
        mapping("a").fields(
          keywordField("foo")
        )
      )
    }.await

    http.execute {
      putMapping("putrawtest" / "a").rawSource(raw)
    }.await

    http.execute {
      getMapping("putrawtest" / "a")
    }.await shouldBe List(IndexMappings("putrawtest", Map("a" -> Map("foo" -> Map("type" -> "keyword")))))
  }
}

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
    }.await shouldBe Seq(IndexMappings("putmaptest",Map("a" -> Map("foo" -> Map("type" -> "keyword"), "moo" -> Map("type" -> "keyword")))))
  }
}

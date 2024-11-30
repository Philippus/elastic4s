package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.mappings.MappingDefinition
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class MappingPropertiesTest extends AnyFunSuite with DockerTests
    with Matchers with ElasticDsl {

  Try {
    client.execute {
      deleteIndex("pcat")
    }.await
  }

  client.execute {
    val name    = TextField("product_name")
    val upc     = KeywordField("upc")
    val a       = LongField("a")
    val b       = IntegerField("b")
    val c       = BooleanField("c")
    val mapping = MappingDefinition(properties = List(name, upc, a, b, c))
    createIndex("pcat").mapping(mapping)
  }.await

  test("get mapping should return created fields") {
    client.execute {
      getMapping("pcat")
    }.await.result.head.mappings shouldBe Map(
      "a"            -> Map("type" -> "long"),
      "upc"          -> Map("type" -> "keyword"),
      "b"            -> Map("type" -> "integer"),
      "product_name" -> Map("type" -> "text"),
      "c"            -> Map("type" -> "boolean")
    )
  }

  test("mappings should support keyword and text fields") {

    client.execute {
      bulk(
        indexInto("pcat").fields("name" -> "gascon malbec", "upc" -> "i89--iwe krwr") id "1",
        indexInto("pcat").fields("name" -> "alamoas malbec", "upc" -> "TTT6 weWER3") id "2"
      ).refresh(RefreshPolicy.WAIT_FOR)
    }.await

    client.execute {
      search("pcat").query(termQuery("upc", "i89"))
    }.await.result.totalHits.shouldBe(0)

    val result1 = client.execute {
      search("pcat").query(termQuery("upc", "i89--iwe krwr"))
    }.await

    result1.result.totalHits.shouldBe(1)
    result1.result.hits.hits(0).sourceAsMap shouldBe Map("name" -> "gascon malbec", "upc" -> "i89--iwe krwr")

    val result2 = client.execute {
      search("pcat").query(termQuery("name", "malbec"))
    }.await

    result2.result.totalHits.shouldBe(2)
    result2.result.hits.hits(0).sourceAsMap shouldBe Map("name" -> "gascon malbec", "upc" -> "i89--iwe krwr")
    result2.result.hits.hits(1).sourceAsMap shouldBe Map("name" -> "alamoas malbec", "upc" -> "TTT6 weWER3")
  }
}

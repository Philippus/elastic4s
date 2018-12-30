package com.sksamuel.elastic4s.requests.get

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class GetTest extends FlatSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("beer")
    }.await
  }

  client.execute {
    createIndex("beer").mappings {
      mapping("lager").fields(
        textField("name").stored(true),
        textField("brand").stored(true),
        textField("ingredients").stored(true)
      )
    }
  }.await

  client.execute {
    bulk(
      indexInto("beer/lager") fields(
        "name" -> "coors light",
        "brand" -> "coors",
        "ingredients" -> Seq("hops", "barley", "water", "yeast")
      ) id "4",
      indexInto("beer/lager") fields(
        "name" -> "bud lite",
        "brand" -> "bud",
        "ingredients" -> Seq("hops", "barley", "water", "yeast")
      ) id "8"
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "A Get request" should "retrieve a document by id" in {

    val resp = client.execute {
      get("8") from "beer"
    }.await.result

    resp.exists shouldBe true
    resp.id shouldBe "8"
  }

  it should "retrieve a document by id with source" in {

    val resp = client.execute {
      get("8") from "beer"
    }.await.result

    resp.exists shouldBe true
    resp.id shouldBe "8"
    resp.sourceAsMap.keySet shouldBe Set("name", "brand", "ingredients")
  }

  it should "retrieve a document by id without source" in {

    val resp = client.execute {
      get("8") from "beer/lager" fetchSourceContext false
    }.await.result

    resp.exists should be(true)
    resp.id shouldBe "8"
    resp.sourceAsMap shouldBe Map.empty
    resp.storedFieldsAsMap shouldBe Map.empty
  }

  it should "support source includes" in {

    val resp = client.execute {
      get("8") from "beer/lager" fetchSourceInclude "brand"
    }.await.result

    resp.exists should be(true)
    resp.id shouldBe "8"
    resp.sourceAsMap shouldBe Map("brand" -> "bud")
  }

  it should "support source excludes" in {

    val resp = client.execute {
      get("8") from "beer/lager" fetchSourceExclude "brand"
    }.await.result

    resp.exists should be(true)
    resp.id shouldBe "8"
    resp.sourceAsMap shouldBe Map("name" -> "bud lite", "ingredients" -> List("hops", "barley", "water", "yeast"))
  }

  it should "support source includes and excludes" in {

    val resp = client.execute {
      get("8") from "beer/lager" fetchSourceContext(List("name"), List("brand"))
    }.await.result

    resp.exists should be(true)
    resp.id shouldBe "8"
    resp.sourceAsMap shouldBe Map("name" -> "bud lite")
  }

  it should "retrieve a document supporting stored fields" in {

    val resp = client.execute {
      get("4") from "beer/lager" storedFields("name", "brand")
    }.await.result

    resp.exists should be(true)
    resp.id shouldBe "4"
    resp.storedFieldsAsMap.keySet shouldBe Set("name", "brand")
    resp.storedField("name").value shouldBe "coors light"
  }

  it should "retrieve multi value fields" in {

    val resp = client.execute {
      get("4") from "beer/lager" storedFields "ingredients"
    }.await.result

    val field = resp.storedField("ingredients")
    field.values shouldBe Seq("hops", "barley", "water", "yeast")
  }

  it should "return Left[RequestFailure] when index does not exist" in {
    client.execute {
      get("4") from "qqqqqqqqqq"
    }.await.error.`type` shouldBe "index_not_found_exception"
  }

  it should "return Right with exists=false when the doc does not exist" in {
    client.execute {
      get("111111") from "beer"
    }.await.result.exists shouldBe false
  }
}

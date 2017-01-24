package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.testkit.{ElasticSugar}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

class TcpGetTest extends FlatSpec with Matchers with ScalaFutures with ElasticSugar {

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
      ) id 4,
      indexInto("beer/lager") fields("name" -> "bud lite", "brand" -> "bud") id 8
    )
  }.await

  refresh("beer")
  blockUntilCount(2, "beer")

  "A Get request" should "retrieve a document by id" in {

    val resp = client.execute {
      get(8) from "beer/lager"
    }.await
    resp.id shouldBe "8"
  }

  it should "retrieve a document by id with source" in {

    val resp = client.execute {
      get(8) from "beer/lager"
    }.await

    resp.exists should be(true)
    resp.id shouldBe "8"
    resp.sourceAsMap.keySet shouldBe Set("name", "brand")
  }

  it should "retrieve a document by id without source" in {

    val resp = client.execute {
      get(8) from "beer/lager" fetchSourceContext false
    }

    whenReady(resp) { r =>
      r.exists should be(true)
      r.id shouldBe "8"
      r.sourceAsMap shouldBe Map.empty
      r.fields should have size 0
    }
  }

  it should "retrieve a document by id with name field and without source" in {

    val resp = client.execute {
      get id 8 from "beer/lager" storedFields "name" fetchSourceContext false
    }

    whenReady(resp) { r =>
      r.exists should be(true)
      r.id shouldBe "8"
      r.sourceAsMap shouldBe Map.empty
      r.fields should (contain key "name" and not contain key("brand"))
    }
  }

  it should "retrieve a document by id with storedFields=name,brand and source=true" in {

    val resp = client.execute {
      get(4) from "beer/lager" storedFields("name", "brand") fetchSourceContext true
    }

    whenReady(resp) { r =>
      r.exists should be(true)
      r.id shouldBe "4"
      r.sourceAsMap.keySet shouldBe Set("name", "brand", "ingredients")
      r.fields.keySet shouldBe Set("name", "brand")
    }
  }

  it should "not retrieve any documents w/ unknown id" in {

    val resp = client.execute {
      get(131313) from "beer/lager"
    }

    whenReady(resp) { result =>
      result.exists shouldBe false
    }
  }

  it should "retrieve multi value fields" in {
    val resp = client.execute {
      get(4) from "beer/lager" storedFields "ingredients"
    }

    whenReady(resp) { r =>
      val field = r.field("ingredients")
      field.values shouldBe Seq("hops", "barley", "water", "yeast")
    }
  }

}

package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

/** @author Stephen Samuel */
class GetTest extends FlatSpec with Matchers with ScalaFutures with ElasticSugar {

  client.execute {
    bulk(
      index into "beer/lager" fields ("name" -> "coors light", "brand" -> "coors", "ingredients" -> Seq("hops",
        "barley",
        "water",
        "yeast")) id 4,
      index into "beer/lager" fields ("name" -> "bud lite", "brand" -> "bud") id 8
    )
  }.await

  refresh("beer")
  blockUntilCount(2, "beer")

  "A Get request" should "retrieve a document by id" in {

    val resp = client.execute {
      get id 8 from "beer/lager"
    }.await
    resp.id shouldBe "8"
  }

  it should "retrieve a document asynchronously by id" in {

    val resp = client.execute {
      get id 8 from "beer/lager"
    }

    whenReady(resp) { result =>
      result.isExists should be(true)
      result.id shouldBe "8"
    }
  }

  it should "retrieve a document asynchronously by id w/ source" in {

    val resp = client.execute {
      get id 8 from "beer/lager"
    }

    whenReady(resp) { result =>
      result.isExists should be(true)
      result.id shouldBe "8"
      result.source should not be null
      result.fields should have size 0
    }
  }

  it should "retrieve a document asynchronously by id w/o source" in {

    val resp = client.execute {
      get id 8 from "beer/lager" fetchSourceContext false
    }

    whenReady(resp) { result =>
      result.isExists should be(true)
      result.id shouldBe "8"
      result.source shouldBe Map.empty
      result.fields should have size 0
    }
  }

  it should "retrieve a document asynchronously by id w/ name and w/o source" in {

    val resp = client.execute {
      get id 8 from "beer/lager" fields "name"
    }

    whenReady(resp) { result =>
      result.isExists should be(true)
      result.id shouldBe "8"
      result.source shouldBe Map.empty
      result.fields should (contain key "name" and not contain key("brand"))
    }
  }

  it should "retrieve a document asynchronously by id w/ name and brand and source" in {

    val resp = client.execute {
      get id 4 from "beer/lager" fields "name" fetchSourceContext true
    }

    whenReady(resp) { result =>
      result.isExists should be(true)
      result.id shouldBe "4"
      result.source should not be null
      result.fields should (contain key "name" and not contain key("brand"))
    }
  }

  it should "not retrieve any documents w/ unknown id" in {

    val resp = client.execute {
      get id 1 from "beer/lager" fields "name" fetchSourceContext true
    }

    whenReady(resp) { result =>
      result.isExists should be(false)
    }
  }

  it should "retrieve multi value fields" in {
    val resp = client.execute {
      get id 4 from "beer/lager" fields "ingredients"
    }
    whenReady(resp) {
      result => println(result.field("ingredients").getValues.asScala)
    }
  }

}

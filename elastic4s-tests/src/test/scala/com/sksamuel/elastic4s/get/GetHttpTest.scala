package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.{AbstractElasticSugar, ClassloaderLocalNodeProvider, ElasticSugar, SharedElasticSugar}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

class GetHttpTest extends FlatSpec with Matchers with ScalaFutures with SharedElasticSugar with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

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

    val resp = http.execute {
      get(8) from "beer/lager"
    }.await

    resp.exists shouldBe true
    resp.id shouldBe "8"
  }

  it should "retrieve a document by id with source" in {

    val resp = http.execute {
      get(8) from "beer/lager"
    }.await

    resp.exists shouldBe true
    resp.id shouldBe "8"
    resp.sourceAsMap.keySet shouldBe Set("name", "brand")
  }

  it should "retrieve a document by id without source" in {

    val resp = http.execute {
      get(8) from "beer/lager" fetchSourceContext false
    }

    whenReady(resp) { response =>
      response.exists should be(true)
      response.id shouldBe "8"
      response.sourceAsMap shouldBe Map.empty
      response.storedFieldsAsMap shouldBe Map.empty
    }
  }

  it should "retrieve a document supporting stored fields" in {

    val resp = http.execute {
      get(4) from "beer/lager" storedFields("name", "brand")
    }

    whenReady(resp) { response =>
      response.exists should be(true)
      response.id shouldBe "4"
      response.storedFieldsAsMap.keySet shouldBe Set("name", "brand")
    }
  }

  //  it should "throw exception for unknown doc" ignore {
  //
  //    val resp = http.execute {
  //      get(131313) from "beer/lager"
  //    }
  //
  //    whenReady(resp) { response =>
  //      response.exists shouldBe false
  //    }
  //  }

  it should "retrieve multi value fields" in {

    val resp = http.execute {
      get(4) from "beer/lager" storedFields "ingredients"
    }

    whenReady(resp) { response =>
      val field = response.storedField("ingredients")
      field.values shouldBe Seq("hops", "barley", "water", "yeast")
    }
  }
}

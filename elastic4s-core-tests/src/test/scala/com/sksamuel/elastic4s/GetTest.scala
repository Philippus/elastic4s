package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.{ FlatSpec, Matchers }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar

/** @author Stephen Samuel */
class GetTest extends FlatSpec with Matchers with ScalaFutures with MockitoSugar with ElasticSugar {

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
    assert("8" === resp.getId)
  }

  it should "retrieve a document asynchronously by id" in {

    val resp = client.execute {
      get id 8 from "beer/lager"
    }

    whenReady(resp) { result =>
      result.isExists should be(true)
      result.getId shouldBe "8"
    }
  }

  it should "retrieve a document asynchronously by id w/ source" in {

    val resp = client.execute {
      get id 8 from "beer/lager"
    }

    whenReady(resp) { result =>
      result.isExists should be(true)
      result.getId shouldBe "8"
      result.getSource should not be null
      result.getFields should have size 0
    }
  }

  it should "retrieve a document asynchronously by id w/o source" in {

    val resp = client.execute {
      get id 8 from "beer/lager" fetchSourceContext false
    }

    whenReady(resp) { result =>
      result.isExists should be(true)
      result.getId shouldBe "8"
      result.getSource should be(null)
      result.getFields should have size 0
    }
  }

  it should "retrieve a document asynchronously by id w/ name and w/o source" in {

    val resp = client.execute {
      get id 8 from "beer/lager" fields "name"
    }

    whenReady(resp) { result =>
      result.isExists should be(true)
      result.getId shouldBe "8"
      result.getSource should be(null)
      result.getFields should (contain key "name" and not contain key("brand"))
    }
  }

  it should "retrieve a document asynchronously by id w/ name and brand and source" in {

    val resp = client.execute {
      get id 4 from "beer/lager" fields "name" fetchSourceContext true
    }

    whenReady(resp) { result =>
      result.isExists should be(true)
      result.getId shouldBe "4"
      result.getSource should not be null
      result.getFields should (contain key "name" and not contain key("brand"))
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

  import scala.collection.JavaConverters._

  it should "retrieve multi value fields" in {
    val resp = client.execute {
      get id 4 from "beer/lager" fields "ingredients"
    }
    whenReady(resp) {
      result => println(result.getField("ingredients").getValues.asScala)
    }
  }

}

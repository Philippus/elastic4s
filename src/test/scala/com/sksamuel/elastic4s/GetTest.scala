package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.common.Priority
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.Matchers

/** @author Stephen Samuel */
class GetTest extends FlatSpec with Matchers with ScalaFutures with MockitoSugar with ElasticSugar {

  client.execute {
    index into "beer/lager" fields(
      "name" -> "coors light",
      "brand" -> "coors"
      ) id 4
  }
  client.execute {
    index into "beer/lager" fields(
      "name" -> "bud lite",
      "brand" -> "bud"
      ) id 8
  }

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("beer")
  blockUntilCount(2, "beer")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "A Get request" should "retrieve a document by id" in {

    val resp = client.sync.execute {
      get id 8 from "beer/lager"
    }
    assert("8" === resp.getId)
  }

  it should "retrieve a document asynchronously by id" in {

    val resp = client.execute {
      get id 8 from "beer/lager"
    }

    whenReady(resp) { result =>
      result.isExists should be (true)
      result.getId shouldBe "8"
    }
  }

  it should "retrieve a document asynchronously by id w/ source" in {

    val resp = client.execute {
      get id 8 from "beer/lager"
    }

    whenReady(resp) { result =>
      result.isExists should be (true)
      result.getId shouldBe "8"
      result.getSource should not be (null)
      result.getFields should have size 0
    }
  }

  it should "retrieve a document asynchronously by id w/o source" in {

    val resp = client.execute {
      get id 8 from "beer/lager" fetchSourceContext false
    }

    whenReady(resp) { result =>
      result.isExists should be (true)
      result.getId shouldBe "8"
      result.getSource should be (null)
      result.getFields should have size 0
    }
  }

  it should "retrieve a document asynchronously by id w/ name and w/o source" in {

    val resp = client.execute {
      get id 8 from "beer/lager" fields("name")
    }

    whenReady(resp) { result =>
      result.isExists should be (true)
      result.getId shouldBe "8"
      result.getSource should be (null)
      result.getFields should (contain key ("name") and not contain key ("brand"))
    }
  }

  it should "retrieve a document asynchronously by id w/ name and brand and source" in {

    val resp = client.execute {
      get id 4 from "beer/lager" fields("name") fetchSourceContext true
    }

    whenReady(resp) { result =>
      result.isExists should be (true)
      result.getId shouldBe "4"
      result.getSource should not be (null)
      result.getFields should (contain key ("name") and not contain key ("brand"))
    }
  }

  it should "not retrieve any documents w/ unknown id" in {

    val resp = client.execute {
      get id 1 from "beer/lager" fields("name") fetchSourceContext true
    }

    whenReady(resp) { result =>
      result.isExists should be (false)
    }
  }

}

package com.sksamuel.elastic4s.requests.get

import com.sksamuel.elastic4s.{Authentication, CommonRequestOptions}
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.mockito.MockitoSugar

import scala.util.Try

class MultiGetTest extends AnyFlatSpec with MockitoSugar with DockerTests {

  Try {
    client.execute {
      deleteIndex("coldplay")
    }.await
  }

  client.execute {
    createIndex("coldplay").shards(2).mapping(
      mapping(
        textField("name").stored(true),
        intField("year").stored(true)
      )
    )
  }.await

  client.execute(
    bulk(
      indexInto("coldplay") id "1" fields ("name" -> "parachutes", "year"    -> 2000),
      indexInto("coldplay") id "3" fields ("name" -> "x&y", "year"           -> 2005),
      indexInto("coldplay") id "5" fields ("name" -> "mylo xyloto", "year"   -> 2011),
      indexInto("coldplay") id "7" fields ("name" -> "ghost stories", "year" -> 2015)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "a multiget request" should "retrieve documents by id" in {

    val resp = client.execute(
      multiget(
        get("coldplay", "3"),
        get("coldplay", "5"),
        get("coldplay", "7")
      )
    ).await.result

    resp.size shouldBe 3

    resp.items.head.id shouldBe "3"
    resp.items.head.exists shouldBe true

    resp.items(1).id shouldBe "5"
    resp.items(1).exists shouldBe true

    resp.items.last.id shouldBe "7"
    resp.items.last.exists shouldBe true
  }

  it should "set exists=false for missing documents" in {

    val resp = client.execute(
      multiget(
        get("coldplay", "3"),
        get("coldplay", "711111")
      )
    ).await.result

    resp.size shouldBe 2
    resp.items.head.exists shouldBe true
    resp.items.last.exists shouldBe false
  }

  it should "retrieve documents by id with selected fields" in {

    val resp = client.execute(
      multiget(
        get("coldplay", "3") storedFields ("name", "year"),
        get("coldplay", "5") storedFields "name"
      )
    ).await.result

    resp.size shouldBe 2
    resp.items.head.fields shouldBe Map("year" -> List(2005), "name" -> List("x&y"))
    resp.items.last.fields shouldBe Map("name" -> List("mylo xyloto"))
  }

  it should "retrieve documents by id with fetchSourceContext" in {

    val resp = client.execute(
      multiget(
        get("coldplay", "3") fetchSourceContext Seq("name", "year"),
        get("coldplay", "5") fetchSourceContext Seq("name")
      )
    ).await.result
    resp.size shouldBe 2
    resp.items.head.source shouldBe Map("year" -> 2005, "name" -> "x&y")
    resp.items.last.source shouldBe Map("name" -> "mylo xyloto")
  }
  it should "retrieve documents by id with routing spec" in {

    val resp = client.execute(
      multiget(get("coldplay", "3") routing "3")
    ).await.result

    resp.size shouldBe 1

    resp.items.head.id shouldBe "3"
    resp.items.head.exists shouldBe true
  }

  it should "error if authentication is unsuccessful" in {
    implicit val requestOptions: CommonRequestOptions = CommonRequestOptions.defaults.copy(
      authentication = Authentication.UsernamePassword("not_exists", "pass123")
    )

    client.execute(
      multiget(get("coldplay", "3") routing "3")
    ).await.error.`type` shouldBe "security_exception"
  }
}

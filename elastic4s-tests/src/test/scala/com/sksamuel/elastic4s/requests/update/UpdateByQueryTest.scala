package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers, OptionValues}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

class UpdateByQueryTest
  extends FlatSpec
    with Matchers
    with DockerTests
    with OptionValues {

  Try {
    client.execute {
      deleteIndex("pop")
    }.await
  }

  Try {
    client.execute {
      createIndex("pop")
    }.await
  }

  client.execute {
    bulk(
      indexInto("pop").fields("name" -> "sprite", "type" -> "lemonade", "foo" -> "f"),
      indexInto("pop").fields("name" -> "fanta", "type" -> "orangeade", "foo" -> "f"),
      indexInto("pop").fields("name" -> "pepsi", "type" -> "cola", "foo" -> "f")
    ).refreshImmediately
  }.await

  "an update by query request" should "support script based update" in {

    client.execute {
      updateByQuery("pop", matchAllQuery()).script(script("ctx._source.foo = 'a'").lang("painless")).refreshImmediately
    }.await.result.updated shouldBe 3

    client.execute {
      count("pop").query(termQuery("foo", "a"))
    }.await.result.count shouldBe 3
  }

  it should "return errors if the update fails" in {
    client.execute {
      updateByQuery("pop", prefixQuery("name", "spr")).script("wibble")
    }.await.error.`type` shouldBe "script_exception"
  }

  it should "support RefreshPolicy.IMMEDIATE" in {
    client.execute {
      updateByQuery("pop", matchAllQuery()).script(script("ctx._source.foo = 'b'").lang("painless")).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result.updated shouldBe 3

    client.execute {
      count("pop").query(termQuery("foo", "b"))
    }.await.result.count shouldBe 3
  }

  it should "support RefreshPolicy.NONE" in {
    client.execute {
      updateByQuery("pop", matchAllQuery()).script(script("ctx._source.foo = 'c'").lang("painless")).refresh(RefreshPolicy.NONE)
    }.await.result.updated shouldBe 3

    client.execute {
      count("pop").query(termQuery("foo", "c"))
    }.await.result.count shouldBe 0
  }
}

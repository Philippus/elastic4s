package com.sksamuel.elastic4s.requests.update

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
      indexInto("pop/pop").fields("name" -> "sprite", "type" -> "lemonade", "foo" -> "f"),
      indexInto("pop/pop").fields("name" -> "fanta", "type" -> "orangeade", "foo" -> "f"),
      indexInto("pop/pop").fields("name" -> "pepsi", "type" -> "cola", "foo" -> "f")
    ).refreshImmediately
  }.await

  "an update by query request" should "support script based update" in {

    client.execute {
      updateByQuery("pop", "pop", matchAllQuery()).script(script("ctx._source.foo = 'bar'").lang("painless")).refreshImmediately
    }.await.result.updated shouldBe 3

    client.execute {
      count("pop").query(termQuery("foo", "bar"))
    }.await.result.count shouldBe 3
  }

  it should "return errors if the update fails" in {
    client.execute {
      updateByQuery("pop", "pop", prefixQuery("name", "spr")).script("wibble")
    }.await.error.`type` shouldBe "script_exception"
  }
}

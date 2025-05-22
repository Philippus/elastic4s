package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Try

import com.sksamuel.elastic4s.requests.script.Script
import org.scalatest.concurrent.Eventually

class UpdateByQueryTest
    extends AnyFlatSpec
    with Matchers
    with DockerTests
    with OptionValues
    with Eventually {

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
      indexInto("pop").fields("name" -> "sprite", "type" -> "lemonade", "foo"  -> "f"),
      indexInto("pop").fields("name" -> "fanta", "type"  -> "orangeade", "foo" -> "f"),
      indexInto("pop").fields("name" -> "pepsi", "type"  -> "cola", "foo"      -> "f")
    ).refreshImmediately
  }.await

  "an update by query request" should "support script based update" in {

    client.execute {
      updateByQuerySync(
        "pop",
        matchAllQuery()
      ).script(Script("ctx._source.foo = 'a'").lang("painless")).refreshImmediately
    }.await.result.updated shouldBe 3

    client.execute {
      count("pop").query(termQuery("foo", "a"))
    }.await.result.count shouldBe 3
  }

  it should "return errors if the update fails" in {
    client.execute {
      updateByQuerySync("pop", prefixQuery("name", "spr")).script("wibble")
    }.await.error.`type` shouldBe "script_exception"
  }

  it should "support RefreshPolicy.IMMEDIATE" in {
    client.execute {
      updateByQuerySync("pop", matchAllQuery()).script(Script("ctx._source.foo = 'b'").lang("painless")).refresh(
        RefreshPolicy.IMMEDIATE
      )
    }.await.result.updated shouldBe 3

    client.execute {
      count("pop").query(termQuery("foo", "b"))
    }.await.result.count shouldBe 3
  }

  it should "support automatic slicing" in {
    client.execute {
      updateByQuerySync(
        "pop",
        matchAllQuery()
      ).script(Script("ctx._source.foo = 'd'").lang("painless")).automaticSlicing()
        .refresh(RefreshPolicy.IMMEDIATE)
    }.await.result.updated shouldBe 3

    client.execute {
      count("pop").query(termQuery("foo", "d"))
    }.await.result.count shouldBe 3
  }

  it should "support RefreshPolicy.NONE" in {
    client.execute {
      updateByQuerySync("pop", matchAllQuery()).script(Script("ctx._source.foo = 'c'").lang("painless")).refresh(
        RefreshPolicy.NONE
      )
    }.await.result.updated shouldBe 3

    client.execute {
      count("pop").query(termQuery("foo", "c"))
    }.await.result.count shouldBe 0
  }

  it should "support asynchronous update" in {
    client.execute {
      bulk(
        indexInto("pop").fields("name" -> "coca", "type" -> "cola", "foo" -> "g")
      ).refreshImmediately
    }.await

    val task = client.execute {
      updateByQueryAsync(
        "pop",
        termsQuery("name", "coca")
      ).script(Script("ctx._source.foo = 'h'").lang("painless")).refreshImmediately
    }.await.result.task

    eventually {
      client.execute(task).await.result.completed shouldBe true
    }

    client.execute {
      count("pop").query(termQuery("foo", "h"))
    }.await.result.count shouldBe 1
  }
}

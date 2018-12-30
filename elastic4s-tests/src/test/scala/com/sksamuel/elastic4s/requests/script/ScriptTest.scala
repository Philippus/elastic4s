package com.sksamuel.elastic4s.requests.script

import com.sksamuel.elastic4s.testkit.{DockerTests, ElasticMatchers}
import org.scalatest.FreeSpec

import scala.util.Try

class ScriptTest extends FreeSpec with ElasticMatchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("script")
    }.await
  }

  client.execute {
    createIndex("script").mappings(
      mapping("tubestops").fields(
        textField("name").fielddata(true),
        textField("line").fielddata(true)
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("script/tubestops") fields("name" -> "south kensington", "line" -> "district"),
      indexInto("script/tubestops") fields("name" -> "earls court", "line" -> "district", "zone" -> 2),
      indexInto("script/tubestops") fields("name" -> "cockfosters", "line" -> "picadilly"),
      indexInto("script/tubestops") fields("name" -> "bank", "line" -> "northern")
    ).refreshImmediately
  }.await

  "script fields" - {
    "can access doc fields" in {
      val result = client.execute {
        search("script").matchQuery("name", "cockfosters").scriptfields(
          scriptField("a", "doc['line'].value")
        )
      }.await.result
      result.hits.hits.head.storedField("a").value shouldBe "picadilly"
    }
    "can use params" in {
      val result = client.execute {
        search("script") query "earls" scriptfields (
          scriptField("a") script (
            script("doc['zone'].value * params.fare") params Map("fare" -> 4.50)
            )
          )
      }.await.result
      result.hits.hits.head.storedField("a").value shouldBe 9.0
    }
  }
}

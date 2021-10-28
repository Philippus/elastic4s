package com.sksamuel.elastic4s.search.sort

import com.sksamuel.elastic4s.requests.searches.sort.ScriptSortType
import com.sksamuel.elastic4s.testkit.{DockerTests, ElasticMatchers}
import org.scalatest.freespec.AnyFreeSpec

import scala.util.Try

class ScriptSortTest extends AnyFreeSpec with ElasticMatchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("scriptsort")
    }.await
  }

  client.execute {
    createIndex("scriptsort").mapping(
      mapping(
        textField("name").fielddata(true),
        textField("line").fielddata(true)
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("scriptsort") fields("name" -> "south kensington", "line" -> "district"),
      indexInto("scriptsort") fields("name" -> "earls court", "line" -> "district"),
      indexInto("scriptsort") fields("name" -> "bank", "line" -> "northern")
    ).refreshImmediately
  }.await

  "script sort" - {
    "sort by name length" in {
      val sorted = client.execute {
        search("scriptsort") query matchAllQuery() sortBy {
          scriptSort(
            script(""" doc['name'].value.length() """)
          ) typed ScriptSortType.NUMBER
        }
      }.await.result
      sorted.hits.hits(0).sourceAsMap("name") shouldBe "bank"
      sorted.hits.hits(1).sourceAsMap("name") shouldBe "earls court"
      sorted.hits.hits(2).sourceAsMap("name") shouldBe "south kensington"
    }
  }

}

package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder
import org.scalatest.{ FreeSpec, Matchers }

class ScriptTest extends FreeSpec with Matchers with ElasticSugar {

  client.execute {
    bulk(
      index into "script/tubestops" fields ("name" -> "south kensington", "line" -> "district"),
      index into "script/tubestops" fields ("name" -> "earls court", "line" -> "district", "zone" -> 2),
      index into "script/tubestops" fields ("name" -> "cockfosters", "line" -> "picadilly"),
      index into "script/tubestops" fields ("name" -> "bank", "line" -> "northern")
    )
  }.await

  blockUntilCount(4, "script")

  "script fields" - {
    "can access doc fields" in {

      val resp = client.execute {
        search in "script/tubestops" query "bank" scriptfields (
          script field "a" script "doc['line'].value + ' line'"
        )
      }.await
      resp.getHits.getAt(0).field("a").value[String] shouldBe "northern line"

    }
    "can use params" in {

      val resp = client.execute {
        search in "script/tubestops" query "earls" scriptfields (
          script field "a" script "'Fare is: ' + doc['zone'].value * fare" params Map("fare" -> 4.50)
        )
      }.await
      resp.getHits.getAt(0).field("a").value[String] shouldBe "Fare is: 9.0"

    }
  }
  "script sort" - {
    "sort by name length" in {
      val sorted = client.execute {
        search in "script/tubestops" query matchall sort {
          script sort """if (_source.containsKey('name')) _source['name'].size() else 0""" order SortOrder.DESC typed "number"
        }
      }.await
      sorted.hits(0).sourceAsMap("name") shouldBe "south kensington"
      sorted.hits(3).sourceAsMap("name") shouldBe "bank"
    }
  }

}

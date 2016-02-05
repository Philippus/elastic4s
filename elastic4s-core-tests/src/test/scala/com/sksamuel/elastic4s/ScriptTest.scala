package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.testkit.{ElasticMatchers, ElasticSugar}
import org.elasticsearch.search.sort.SortOrder
import org.scalatest.FreeSpec

class ScriptTest extends FreeSpec with ElasticMatchers with ElasticSugar {

  client.execute {
    bulk(
      index into "script/tubestops" fields("name" -> "south kensington", "line" -> "district"),
      index into "script/tubestops" fields("name" -> "earls court", "line" -> "district", "zone" -> 2),
      index into "script/tubestops" fields("name" -> "cockfosters", "line" -> "picadilly"),
      index into "script/tubestops" fields("name" -> "bank", "line" -> "northern")
    )
  }.await

  blockUntilCount(4, "script")

  "script fields" - {
    "can access doc fields" in {
      search in "script/tubestops" query "bank" scriptfields
        scriptField("a", "doc['line'].value + ' line'") should
        haveFieldValue("northern line")
    }
    "can use params" in {
      search in "script/tubestops" query "earls" scriptfields (
        scriptField("a") script "'Fare is: ' + doc['zone'].value * fare" params Map("fare" -> 4.50)
        ) should haveFieldValue("Fare is: 9.0")
    }
  }
  "script sort" - {
    "sort by name length" in {
      val sorted = client.execute {
        search in "script/tubestops" query matchAllQuery sort {
          scriptSort("""if (_source.containsKey('name')) _source['name'].size() else 0""") typed "number" order SortOrder.DESC
        }
      }.await
      sorted.hits(0).sourceAsMap("name") shouldBe "south kensington"
      sorted.hits(3).sourceAsMap("name") shouldBe "bank"
    }
  }

}

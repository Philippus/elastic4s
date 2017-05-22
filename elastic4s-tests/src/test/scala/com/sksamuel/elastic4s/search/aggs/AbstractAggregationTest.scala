package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, ElasticSugar}
import org.scalatest.{FreeSpec, Matchers}

abstract class AbstractAggregationTest extends FreeSpec with Matchers with ElasticSugar with ClassloaderLocalNodeProvider {

  deleteIndex("aggregations")

  client.execute {
    createIndex("aggregations") mappings {
      mapping("breakingbad") fields(
        keywordField("job"),
        keywordField("actor")
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("aggregations/breakingbad") fields("name" -> "walter white", "job" -> "meth kingpin", "age" -> 50, "actor" -> "bryan"),
      indexInto("aggregations/breakingbad") fields("name" -> "hank schrader", "job" -> "dea agent", "age" -> 55, "actor" -> "dean"),
      indexInto("aggregations/breakingbad") fields("name" -> "jesse pinkman", "job" -> "meth sidekick", "age" -> 30),
      indexInto("aggregations/breakingbad") fields("name" -> "gus fring", "job" -> "meth kingpin", "age" -> 60),
      indexInto("aggregations/breakingbad") fields("name" -> "steven gomez", "job" -> "dea agent", "age" -> 50),
      indexInto("aggregations/breakingbad") fields("name" -> "saul goodman", "job" -> "lawyer", "age" -> 55),
      indexInto("aggregations/breakingbad") fields("name" -> "Huell Babineaux", "job" -> "heavy", "age" -> 43, "actor" -> "lavell"),
      indexInto("aggregations/breakingbad") fields("name" -> "mike ehrmantraut", "job" -> "heavy", "age" -> 45),
      indexInto("aggregations/breakingbad") fields("name" -> "lydia rodarte quayle", "job" -> "meth sidekick", "age" -> 40),
      indexInto("aggregations/breakingbad") fields("name" -> "todd alquist", "job" -> "meth sidekick", "age" -> 26)
    ).immediateRefresh()
  ).await
}

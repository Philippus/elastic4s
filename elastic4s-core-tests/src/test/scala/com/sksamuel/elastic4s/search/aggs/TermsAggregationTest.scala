package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms
import org.scalatest.{FreeSpec, Matchers}

class TermsAggregationTest extends FreeSpec with Matchers with ElasticSugar {

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
    )
  ).await

  refresh("aggregations")
  blockUntilCount(10, "aggregations")

  "terms aggregation" - {
    "should group by field" in {

      val resp = client.execute {
        search("aggregations/breakingbad") aggs {
          termsAggregation("agg1").field("job")
        }
      }.await
      resp.totalHits shouldBe 10

      val agg = resp.aggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
      agg.getBuckets.size shouldBe 5
      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
      agg.getBucketByKey("meth sidekick").getDocCount shouldBe 3
      agg.getBucketByKey("dea agent").getDocCount shouldBe 2
      agg.getBucketByKey("lawyer").getDocCount shouldBe 1
      agg.getBucketByKey("heavy").getDocCount shouldBe 2
    }

    "should only include matching documents in the query" in {
      val resp = client.execute {
        // should match 3 documents: steven, saul, hank Schrader
        search in "aggregations/breakingbad" query prefixQuery("name" -> "s") aggregations {
          aggregation terms "agg1" field "job"
        }
      }.await
      resp.totalHits shouldBe 3
      val aggs = resp.aggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
      aggs.getBuckets.size shouldBe 2
      aggs.getBucketByKey("dea agent").getDocCount shouldBe 2
      aggs.getBucketByKey("lawyer").getDocCount shouldBe 1
    }

    "should only return included fields" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          aggregation terms "agg1" field "job" includeExclude(Seq("meth kingpin", "lawyer"), Nil)
        }
      }.await
      resp.totalHits shouldBe 10
      val agg = resp.aggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
      agg.getBuckets.size shouldBe 2
      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
      agg.getBucketByKey("lawyer").getDocCount shouldBe 1
    }

    "should not return excluded fields" in {

      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          aggregation terms "agg1" field "job" includeExclude(Nil, Iterable("lawyer"))
        }
      }.await
      resp.totalHits shouldBe 10

      val agg = resp.aggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
      agg.getBuckets.size shouldBe 4
      agg.getBucketByKey("meth sidekick").getDocCount shouldBe 3
      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
      agg.getBucketByKey("dea agent").getDocCount shouldBe 2
      agg.getBucketByKey("heavy").getDocCount shouldBe 2
    }

    "should group by field and return a missing value" in {

      val resp = client.execute {
        search("aggregations/breakingbad").aggregations {
          termsAggregation("agg1") field "actor" missing "no-name"
        }
      }.await
      resp.totalHits shouldBe 10

      val agg = resp.aggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
      agg.getBuckets.size shouldBe 4
      agg.getBucketByKey("lavell").getDocCount shouldBe 1
      agg.getBucketByKey("bryan").getDocCount shouldBe 1
      agg.getBucketByKey("dean").getDocCount shouldBe 1
      agg.getBucketByKey("no-name").getDocCount shouldBe 7
    }
  }
}

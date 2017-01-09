package com.sksamuel.elastic4s.search.aggs

import org.elasticsearch.search.aggregations.bucket.terms.StringTerms

class TermsAggregationTest extends AbstractAggregationTest {

  "terms aggregation" - {
    "should group by field" in {

      val resp = client.execute {
        search("aggregations/breakingbad") aggs {
          termsAggregation("agg1").field("job")
        }
      }.await
      resp.totalHits shouldBe 10

      val agg = resp.aggregations.map("agg1").asInstanceOf[StringTerms]
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
        search("aggregations/breakingbad") query prefixQuery("name" -> "s") aggregations {
          termsAggregation("agg1") field "job"
        }
      }.await
      resp.totalHits shouldBe 3
      val aggs = resp.aggregations.map("agg1").asInstanceOf[StringTerms]
      aggs.getBuckets.size shouldBe 2
      aggs.getBucketByKey("dea agent").getDocCount shouldBe 2
      aggs.getBucketByKey("lawyer").getDocCount shouldBe 1
    }

    "should only return included fields" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          termsAggregation("agg1") field "job" includeExclude("lawyer", "")
        }
      }.await
      resp.totalHits shouldBe 10
      val agg = resp.aggregations.map("agg1").asInstanceOf[StringTerms]
      agg.getBuckets.size shouldBe 1
      agg.getBucketByKey("lawyer").getDocCount shouldBe 1
    }

    "should not return excluded fields" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          termsAggregation("agg1") field "job" includeExclude("", "lawyer")
        }
      }.await
      resp.totalHits shouldBe 10


      val agg = resp.aggregations.stringTermsResult("agg1")
      agg.getBuckets.size shouldBe 4
      agg.getBucketByKey("meth sidekick").getDocCount shouldBe 3
      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
      agg.getBucketByKey("dea agent").getDocCount shouldBe 2
      agg.getBucketByKey("heavy").getDocCount shouldBe 2
    }

    "should only return included fields (given a seq)" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          termsAggregation("agg1") field "job" includeExclude(Seq("meth kingpin", "lawyer"), Nil)
        }
      }.await
      resp.totalHits shouldBe 10
      val agg = resp.aggregations.map("agg1").asInstanceOf[StringTerms]
      agg.getBuckets.size shouldBe 2
      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
      agg.getBucketByKey("lawyer").getDocCount shouldBe 1
    }

    "should not return excluded fields (given a seq)" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          termsAggregation("agg1") field "job" includeExclude(Nil, Iterable("lawyer"))
        }
      }.await
      resp.totalHits shouldBe 10

      val agg = resp.aggregations.stringTermsResult("agg1")
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

      val agg = resp.aggregations.stringTermsResult("agg1")
      agg.getBuckets.size shouldBe 4
      agg.getBucketByKey("lavell").getDocCount shouldBe 1
      agg.getBucketByKey("bryan").getDocCount shouldBe 1
      agg.getBucketByKey("dean").getDocCount shouldBe 1
      agg.getBucketByKey("no-name").getDocCount shouldBe 7
    }
  }
}

package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram
import org.elasticsearch.search.aggregations.bucket.missing.InternalMissing
import org.elasticsearch.search.aggregations.bucket.range.InternalRange
import org.elasticsearch.search.aggregations.bucket.range.InternalRange.Bucket
import org.elasticsearch.search.aggregations.bucket.significant.heuristics.MutualInformation.MutualInformationBuilder
import org.elasticsearch.search.aggregations.bucket.significant.{SignificantStringTerms, SignificantTerms}
import org.elasticsearch.search.aggregations.bucket.significant.heuristics.ChiSquare
import org.elasticsearch.search.aggregations.bucket.significant.heuristics.ChiSquare.ChiSquareBuilder
import org.elasticsearch.search.aggregations.bucket.significant.heuristics.GND.GNDBuilder
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality
import org.elasticsearch.search.aggregations.metrics.max.InternalMax
import org.elasticsearch.search.aggregations.metrics.min.InternalMin
import org.elasticsearch.search.aggregations.metrics.scripted.InternalScriptedMetric
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount
import org.scalatest.{ FreeSpec, Matchers }

class AggregationsTest extends FreeSpec with Matchers with ElasticSugar {

  client.execute {
    create index "aggregations" mappings {
      "breakingbad" as (
        "job" typed StringType analyzer KeywordAnalyzer
      )
    }
  }.await

  client.execute(
    bulk(
      index into "aggregations/breakingbad" fields ("name" -> "walter white", "job" -> "meth kingpin", "age" -> 50, "actor" -> "bryan"),
      index into "aggregations/breakingbad" fields ("name" -> "hank schrader", "job" -> "dea agent", "age" -> 55, "actor" -> "dean"),
      index into "aggregations/breakingbad" fields ("name" -> "jesse pinkman", "job" -> "meth sidekick", "age" -> 30),
      index into "aggregations/breakingbad" fields ("name" -> "gus fring", "job" -> "meth kingpin", "age" -> 60),
      index into "aggregations/breakingbad" fields ("name" -> "steven gomez", "job" -> "dea agent", "age" -> 50),
      index into "aggregations/breakingbad" fields ("name" -> "saul goodman", "job" -> "lawyer", "age" -> 55),
      index into "aggregations/breakingbad" fields ("name" -> "Huell Babineaux", "job" -> "heavy", "age" -> 43, "actor" -> "lavell"),
      index into "aggregations/breakingbad" fields ("name" -> "mike ehrmantraut", "job" -> "heavy", "age" -> 45),
      index into "aggregations/breakingbad" fields ("name" -> "lydia rodarte quayle", "job" -> "meth sidekick", "age" -> 40),
      index into "aggregations/breakingbad" fields ("name" -> "todd alquist", "job" -> "meth sidekick", "age" -> 26)
    )
  ).await

  refresh("aggregations")
  blockUntilCount(10, "aggregations")

  "terms aggregation" - {
    "should group by field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation terms "agg1" field "job"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val agg = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
      agg.getBuckets.size shouldBe 5
      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
      agg.getBucketByKey("meth sidekick").getDocCount shouldBe 3
      agg.getBucketByKey("dea agent").getDocCount shouldBe 2
      agg.getBucketByKey("lawyer").getDocCount shouldBe 1
      agg.getBucketByKey("heavy").getDocCount shouldBe 2
    }
    "should only include matching documents in the query" in {
      val resp = client.execute {
        // should match 3 documents
        search in "aggregations/breakingbad" query prefixQuery("name" -> "s") aggregations {
          aggregation terms "agg1" field "job"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 3
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
      aggs.getBuckets.size shouldBe 2
      aggs.getBucketByKey("dea agent").getDocCount shouldBe 2
      aggs.getBucketByKey("lawyer").getDocCount shouldBe 1
    }
  }

  "avg aggregation" - {
    "should average by field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation avg "agg1" field "age"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val agg = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalAvg]
      agg.getValue shouldBe 45.4
    }
    "should only include matching documents in the query" in {
      val resp = client.execute {
        // should match 3 documents
        search in "aggregations/breakingbad" query prefixQuery("name" -> "g") aggregations {
          aggregation avg "agg1" field "age"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 3
      val agg = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalAvg]
      agg.getValue shouldBe 55
    }
  }

  "cardinality aggregation" - {
    "should count distinct values" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation cardinality "agg1" field "job"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalCardinality]
      aggs.getValue shouldBe 5
    }
  }

  "missing aggregation" - {
    "should return documents missing a value" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation missing "agg1" field "actor"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalMissing]
      aggs.getDocCount shouldBe 7
    }
  }

  "max aggregation" - {
    "should count max value for field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation max "agg1" field "age"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalMax]
      aggs.getValue shouldBe 60
    }
  }

  "min aggregation" - {
    "should count min value for field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation min "agg1" field "age"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalMin]
      aggs.getValue shouldBe 26
    }
  }

  "sum aggregation" - {
    "should sum values for field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation sum "agg1" field "age"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalSum]
      aggs.getValue shouldBe 454.0
    }
  }

  "value count aggregation" - {
    "should sum values for field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation count "agg1" field "age"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalValueCount]
      aggs.getValue shouldBe 10
    }
  }

  "range aggregation" - {
    "should range by field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation range "agg1" field "age" ranges (20.0 -> 30.0, 30.0 -> 40.0, 40.0 -> 50.0, 50.0 -> 60.0)
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalRange[Bucket]]
      aggs.getBuckets.size shouldBe 4
      aggs.getBucketByKey("20.0-30.0").getDocCount shouldBe 1
      aggs.getBucketByKey("30.0-40.0").getDocCount shouldBe 1
      aggs.getBucketByKey("40.0-50.0").getDocCount shouldBe 3
      aggs.getBucketByKey("50.0-60.0").getDocCount shouldBe 4
    }
  }

  "histogram aggregation" - {
    "should create intervals by field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation histogram "agg1" field "age" interval 10
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[Histogram]
      aggs.getBuckets.size shouldBe 5
      aggs.getBucketByKey(20).getDocCount shouldBe 1
      aggs.getBucketByKey(30).getDocCount shouldBe 1
      aggs.getBucketByKey(40).getDocCount shouldBe 3
      aggs.getBucketByKey(50).getDocCount shouldBe 4
      aggs.getBucketByKey(60).getDocCount shouldBe 1
    }
    "should respect min_doc_count" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation histogram "agg1" field "age" interval 10 minDocCount 2
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[Histogram]
      aggs.getBuckets.size shouldBe 2
      aggs.getBucketByKey(40).getDocCount shouldBe 3
      aggs.getBucketByKey(50).getDocCount shouldBe 4
    }
    "should respect ordering" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation histogram "agg1" field "age" interval 10 order Histogram.Order.COUNT_DESC
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[Histogram]
      aggs.getBuckets.size shouldBe 5
      aggs.getBuckets.get(0).getKeyAsNumber shouldBe 50
      aggs.getBuckets.get(1).getKeyAsNumber shouldBe 40
    }
  }

  "significant terms aggregation" - {
    "should allow setting the significance heuristic" in {
      val mutualInformationResp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation sigTerms "agg1" field "job" significanceHeuristic new MutualInformationBuilder(false, false)
        }
      }.await
      val gndResp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation sigTerms "agg1" field "job" significanceHeuristic new GNDBuilder(false)
        }
      }.await
      val mutualInformationAggs = mutualInformationResp.getAggregations.getAsMap.get("agg1").asInstanceOf[SignificantStringTerms]
      val gndAggs = gndResp.getAggregations.getAsMap.get("agg1").asInstanceOf[SignificantStringTerms]

      mutualInformationAggs.getBuckets.size() shouldBe 0
      gndAggs.getBuckets.size() shouldBe 1
      gndAggs.getBucketByKey("meth sidekick").getDocCount shouldBe 3
    }
  }

  "scripted metric aggregation" - {
    "should compute a word count on field name" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation.scriptedMetric("agg1")
            .initScript("_agg['wordCount'] = []")
            .mapScript("_agg.wordCount.add(doc['name'].values.size())")
            .combineScript("wc = 0; for(c in _agg.wordCount) { wc += c }; return wc")
            .reduceScript("wc = 0; for(a in _aggs) { wc += a }; return wc")
        }
      }.await
      val agg = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalScriptedMetric]
      agg.aggregation().asInstanceOf[Integer] shouldBe 21
    }
  }

}

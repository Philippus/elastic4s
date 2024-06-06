package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class MinMaxAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("minmaxagg")
    }.await
  }

  Try {
    client.execute {
      deleteIndex("minmaxagg2")
    }.await
  }

  client.execute {
    createIndex("minmaxagg") mapping {
      properties(
        textField("name").fielddata(true),
        intField("height").stored(true),
        dateField("opened").format("strict_date")
      )
    }
  }.await

  client.execute {
    createIndex("minmaxagg2") mapping {
      properties(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute {
    createIndex("minmaxagg3") mapping {
      properties(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("minmaxagg") fields("name" -> "Willis Tower", "height" -> 1244, "opened" -> "1973-09-01"),
      indexInto("minmaxagg") fields("name" -> "Burj Kalifa", "height" -> 2456, "opened" -> "2010-01-04"),
      indexInto("minmaxagg") fields("name" -> "Tower of London", "height" -> 169, "opened" -> "1285-01-01"),
      indexInto("minmaxagg2") fields ("name" -> "building of unknown height")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "max agg" - {
    "should return the max for the context" in {
      val resp = client.execute {
        search("minmaxagg").matchAllQuery().aggs(
          maxAgg("agg1", "height"),
          maxAgg("opened", "opened"),
        )
      }.await.result
      resp.totalHits shouldBe 3
      val agg = resp.aggs.max("agg1")
      agg.value shouldBe Some(2456)
      resp.aggs.max("opened").valueAsString shouldBe Some("2010-01-04")
    }
    "should support results when matching docs do not define the field" in {
      val resp = client.execute {
        search("minmaxagg2").matchAllQuery().aggs {
          maxAgg("agg1", "height")
        }
      }.await.result
      resp.totalHits shouldBe 1
      val agg = resp.aggs.max("agg1")
      agg.value shouldBe None
    }
    "should support results when no documents match" in {
      val resp = client.execute {
        search("minmaxagg3").matchAllQuery().aggs {
          maxAgg("agg1", "height")
        }
      }.await.result
      resp.totalHits shouldBe 0
      val agg = resp.aggs.max("agg1")
      agg.value shouldBe None
    }
  }

  "min agg" - {
    "should return the max for the context" in {
      val resp = client.execute {
        search("minmaxagg").matchAllQuery().aggs(
          minAgg("agg1", "height"),
          minAgg("opened", "opened")
        )
      }.await.result
      resp.totalHits shouldBe 3
      val agg = resp.aggs.min("agg1")
      agg.value shouldBe Some(169)
      resp.aggs.min("opened").valueAsString shouldBe Some("1285-01-01")
    }
    "should support results matching docs do not define the field" in {
      val resp = client.execute {
        search("minmaxagg2").matchAllQuery().aggs {
          minAgg("agg1", "height")
        }
      }.await.result
      resp.totalHits shouldBe 1
      val agg = resp.aggs.max("agg1")
      agg.value shouldBe None
    }
    "should support results when no documents match" in {
      val resp = client.execute {
        search("minmaxagg3").matchAllQuery().aggs {
          minAgg("agg1", "height")
        }
      }.await.result
      resp.totalHits shouldBe 0
      val agg = resp.aggs.max("agg1")
      agg.value shouldBe None
    }
  }
}

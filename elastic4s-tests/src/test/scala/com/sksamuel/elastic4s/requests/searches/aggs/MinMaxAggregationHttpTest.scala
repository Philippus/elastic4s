package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class MinMaxAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

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
    createIndex("minmaxagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute {
    createIndex("minmaxagg2") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute {
    createIndex("minmaxagg3") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("minmaxagg/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("minmaxagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("minmaxagg/buildings") fields("name" -> "Tower of London", "height" -> 169),
      indexInto("minmaxagg2/buildings") fields ("name" -> "building of unknown height")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "max agg" - {
    "should return the max for the context" in {
      val resp = client.execute {
        search("minmaxagg").matchAllQuery().aggs {
          maxAgg("agg1", "height")
        }
      }.await.result
      resp.totalHits shouldBe 3
      val agg = resp.aggs.max("agg1")
      agg.value shouldBe Some(2456)
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
        search("minmaxagg").matchAllQuery().aggs {
          minAgg("agg1", "height")
        }
      }.await.result
      resp.totalHits shouldBe 3
      val agg = resp.aggs.min("agg1")
      agg.value shouldBe Some(169)
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

package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class AggregationAsStringTest extends AnyFunSuite with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("aggstring")
    }.await
  }

  client.execute {
    createIndex("aggstring") mapping {
      properties(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client
    .execute(
      bulk(
        indexInto("aggstring") fields ("name" -> "Willis Tower", "height" -> 1244),
        indexInto("aggstring") fields ("name" -> "Burj Kalifa", "height" -> 2456),
        indexInto("aggstring") fields ("name" -> "Tower of London", "height" -> 169)
      ).refresh(RefreshPolicy.Immediate)
    )
    .await

  test("agg as string should return aggregation json") {
    client
      .execute {
        search("aggstring")
          .matchAllQuery()
          .aggs(
            maxAgg("agg1", "height"),
            sumAgg("agg2", "height"),
            termsAgg("agg3", "name")
          )
      }
      .await
      .result
      .aggregationsAsString shouldBe
      """{"agg1":{"value":2456.0},"agg2":{"value":3869.0},"agg3":{"doc_count_error_upper_bound":0,"sum_other_doc_count":0,"buckets":[{"key":"tower","doc_count":2},{"key":"burj","doc_count":1},{"key":"kalifa","doc_count":1},{"key":"london","doc_count":1},{"key":"of","doc_count":1},{"key":"willis","doc_count":1}]}}"""
  }

  test("agg as string should return empty json when no aggregations are present") {
    client
      .execute {
        search("aggstring").matchAllQuery()
      }
      .await
      .result
      .aggregationsAsString shouldBe "{}"
  }

  test("contains for not existent aggregation should return false") {
    client
      .execute {
        search("aggstring").matchAllQuery()
      }
      .await
      .result
      .aggregations
      .contains("no_agg") shouldBe false
  }

}

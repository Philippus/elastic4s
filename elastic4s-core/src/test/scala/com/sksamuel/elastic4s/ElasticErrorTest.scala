package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.HttpEntity.StringEntity
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source.fromResource

class ElasticErrorTest extends FlatSpec with Matchers with ElasticDsl {

  "ElasticError" should "properly handle an error response with an invalid body" in {
    val error = ElasticError.parse(HttpResponse(123, Some(StringEntity("{", None)), Map()))
    assert(error.reason == "123")
  }

  it should "properly handle an error response with a missing body" in {
    val error = ElasticError.parse(HttpResponse(123, Some(StringEntity("", None)), Map()))
    assert(error.reason == "123")
  }

  it must "parse a large error response including failed_shards" in {
    val error = ElasticError.parse(HttpResponse(123, Some(StringEntity(fromResource("error_response_with_failed_shards.json").mkString, None)), Map()))

    assert(error.`type` == "search_phase_execution_exception")
    assert(error.reason == "all shards failed")
    assert(error.phase.contains("query"))
    assert(error.grouped.contains(true))
    assert(error.failedShards.size == 7)
    assert(error.rootCause.size == 7)

    val failedShard = error.failedShards.find(p ⇒ p.node.contains("X6_5FwQsQOSTMc-4wEjLCA")).get
    assert(failedShard.shard == 0)
    assert(failedShard.index contains "items_landsat_20190129_0")
    assert(failedShard.node.contains("X6_5FwQsQOSTMc-4wEjLCA"))
    assert(failedShard.reason.get.`type` == "query_shard_exception")
    assert(failedShard.reason.get.reason == "failed to create query")
    assert(failedShard.reason.get.indexUuid.contains("2d8the20RkivmZXZDwkp1Q"))
    assert(failedShard.reason.get.causedBy.get.reason == "Points of LinearRing do not form a closed linestring")
  }

}

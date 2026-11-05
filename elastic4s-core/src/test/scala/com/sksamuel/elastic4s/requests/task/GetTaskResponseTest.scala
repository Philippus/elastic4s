package com.sksamuel.elastic4s.requests.task

import com.sksamuel.elastic4s.handlers.task.TaskHandlers
import com.sksamuel.elastic4s.{HttpEntity, HttpResponse}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GetTaskResponseTest extends AnyFunSuite with Matchers with TaskHandlers {

  private def parse(json: String): GetTaskResponse =
    GetTaskHandler.responseHandler
      .handle(HttpResponse(200, Some(HttpEntity.StringEntity(json, Some("UTF-8"))), Map.empty))
      .toOption
      .get

  private val task =
    """{"node":"n1","id":123,"type":"transport","action":"indices:data/write/reindex",
      |"status":{"total":10,"updated":0,"created":10,"deleted":0,"batches":1,"version_conflicts":0,"noops":0,
      |"retries":{"bulk":0,"search":0},"throttled_millis":0,"requests_per_second":-1.0,"throttled_until_millis":0},
      |"description":"reindex","start_time_in_millis":1,"running_time_in_nanos":2,"cancellable":true}""".stripMargin

  test("response fields should be deserialized as Long and Float, not boxed Integer") {
    val resp = parse(
      s"""{"completed":true,"task":$task,"response":{"took":5,"timed_out":false,"total":10,"updated":1,"created":2,
         |"deleted":3,"batches":4,"version_conflicts":5,"noops":6,"retries":{"bulk":0,"search":0},
         |"throttled_millis":7,"requests_per_second":-1,"throttled_until_millis":8,"failures":[]}}""".stripMargin
    )

    val r                = resp.response.get
    // reading through a Long-typed val forces the cast that fails if Jackson produced an Integer
    val longs: Seq[Long] =
      Seq(
        r.took.get,
        r.total.get,
        r.updated.get,
        r.created.get,
        r.deleted.get,
        r.batches.get,
        r.versionConflicts.get,
        r.noops.get,
        r.throttledMillis.get,
        r.throttledUntilMillis.get
      )
    longs shouldBe Seq(5L, 10L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L)
    val rps: Float       = r.requestsPerSecond.get
    rps shouldBe -1F
    r.timedOut shouldBe Some(false)
  }

  test("response should support values above Int range") {
    val big  = Int.MaxValue.toLong + 1
    val resp = parse(s"""{"completed":true,"task":$task,"response":{"took":$big,"total":$big}}""")

    resp.response.get.took shouldBe Some(big)
    resp.response.get.total shouldBe Some(big)
  }

  test("response should be None when the task is not complete") {
    parse(s"""{"completed":false,"task":$task}""").response shouldBe None
  }
}

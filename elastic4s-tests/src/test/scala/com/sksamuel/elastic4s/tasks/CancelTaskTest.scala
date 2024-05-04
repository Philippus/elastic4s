package com.sksamuel.elastic4s.tasks

import com.sksamuel.elastic4s.{RequestFailure, Response}
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.task.Retries
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.DurationInt

class CancelTaskTest extends AnyWordSpec with Matchers with DockerTests {

  cleanIndex("cancel_task_a")
  cleanIndex("cancel_task_b")
  cleanIndex("cancel_task_c")

  client.execute {
    bulk(
      indexInto("cancel_task_a").fields(Map("foo" -> "far")),
      indexInto("cancel_task_a").fields(Map("moo" -> "mar")),
      indexInto("cancel_task_a").fields(Map("moo" -> "mar")),
      indexInto("cancel_task_a").fields(Map("goo" -> "gar"))
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "cancel task" should {
    "cancel task by id" in {
      // kick off a task
      val resp = client.execute {
        reindex("cancel_task_a", "cancel_task_b").waitForCompletion(false)
      }.await.result.right.get

      // use the task id from the above task
      val response = client.execute {
        cancelTaskById(resp.nodeId, resp.taskId)
      }.await
      response.result should be(true)
    }

    "cancel task by node and action" in {
      // kick off a task
      val resp = client.execute {
        reindex("cancel_task_a", "cancel_task_c").waitForCompletion(false)
      }.await.result.right.get

      // use the task id from the above task
      val response = client.execute {
        cancelTasks(resp.nodeId).actions("*reindex")
      }.await

      response.result should be(true)
    }
  }
}



package com.sksamuel.elastic4s.tasks

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

class GetTaskTest extends WordSpec with Matchers with DockerTests {

  cleanIndex("get_task_a")
  cleanIndex("get_task_b")

  client.execute {
    bulk(
      indexInto("get_task_a" / "a").fields(Map("foo" -> "far")),
      indexInto("get_task_a" / "a").fields(Map("moo" -> "mar")),
      indexInto("get_task_a" / "a").fields(Map("moo" -> "mar")),
      indexInto("get_task_a" / "a").fields(Map("goo" -> "gar"))
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "get task" should {
    "return task information" in {

      val start = System.currentTimeMillis()

      // kick off a task
      val resp = client.execute {
        reindex("get_task_a", "get_task_b").waitForCompletion(false)
      }.await.result.right.get

      // use the task id from the above task
      val task = client.execute {
        getTask(resp.nodeId, resp.taskId)
      }.await.result.task

      task.node shouldBe resp.nodeId
      task.id shouldBe resp.taskId
      task.`type` shouldBe "transport"
      task.action shouldBe "indices:data/write/reindex"
      task.description shouldBe "reindex from [get_task_a] to [get_task_b]"
      task.startTimeInMillis >= start shouldBe true
    }
  }
}



package com.sksamuel.elastic4s.requests.task

import com.sksamuel.elastic4s.handlers.task.TaskHandlers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import scala.concurrent.duration.*

class TaskHandlersTest extends AnyFlatSpec with TaskHandlers {

  "GetTaskHandler" should "build a request with node and task id" in {
    val request = GetTaskHandler.build(GetTask("node1", "123"))
    request.method shouldBe "GET"
    request.endpoint shouldBe "/_tasks/node1:123"
    request.params shouldBe empty
    request.entity shouldBe empty
  }

  it should "include wait_for_completion when set" in {
    GetTaskHandler.build(GetTask("node1", "123").waitForCompletion(true)).params shouldBe
      Map("wait_for_completion" -> "true")
  }

  it should "include timeout in milliseconds when set" in {
    GetTaskHandler.build(GetTask("node1", "123").timeout(5.seconds)).params shouldBe Map("timeout" -> "5000ms")
  }

  it should "include all parameters when set" in {
    GetTaskHandler.build(GetTask("node1", "123").waitForCompletion(false).timeout(2.seconds)).params shouldBe
      Map("wait_for_completion" -> "false", "timeout" -> "2000ms")
  }

  "ListTaskHandler" should "build a bare request" in {
    val request = ListTaskHandler.build(ListTasks(Nil))
    request.method shouldBe "GET"
    request.endpoint shouldBe "/_tasks"
    request.params shouldBe empty
  }

  it should "include nodes, actions, group_by and parent_task_id" in {
    val request = ListTaskHandler.build(
      ListTasks(Seq("n1", "n2")).actions("a1", "a2").groupByParents().parentTaskId("n1:1")
    )
    request.params shouldBe Map(
      "nodes"          -> "n1,n2",
      "actions"        -> "a1,a2",
      "group_by"       -> "parents",
      "parent_task_id" -> "n1:1"
    )
  }

  it should "send detailed and wait_for_completion even when false" in {
    val request = ListTaskHandler.build(ListTasks(Nil, detailed = Some(false)).waitForCompletion(false))
    request.params shouldBe Map("detailed" -> "false", "wait_for_completion" -> "false")
  }

  it should "include timeout and master_timeout" in {
    val request = ListTaskHandler.build(ListTasks(Nil).timeout(3.seconds).masterTimeout(1.second))
    request.params shouldBe Map("timeout" -> "3000ms", "master_timeout" -> "1000ms")
  }

  "CancelTaskHandler" should "build a bare request" in {
    val request = CancelTaskHandler.build(CancelTasksRequest(Nil))
    request.method shouldBe "POST"
    request.endpoint shouldBe "/_tasks/_cancel"
    request.params shouldBe empty
  }

  it should "include nodes and actions" in {
    CancelTaskHandler.build(CancelTasksRequest(Seq("n1")).actions("a1", "a2")).params shouldBe
      Map("nodes" -> "n1", "actions" -> "a1,a2")
  }

  it should "include timeout, parent_task_id and wait_for_completion" in {
    val request = CancelTaskHandler.build(
      CancelTasksRequest(Nil).timeout(3.seconds).parentTaskId("n1:1").waitForCompletion(true)
    )
    request.params shouldBe Map(
      "timeout"             -> "3000ms",
      "parent_task_id"      -> "n1:1",
      "wait_for_completion" -> "true"
    )
  }

  "CancelTaskByIdHandler" should "build the cancel endpoint" in {
    val request = CancelTaskByIdHandler.build(CancelTaskByIdRequest("node1", "123"))
    request.method shouldBe "POST"
    request.endpoint shouldBe "/_tasks/node1:123/_cancel"
    request.params shouldBe empty
  }

  it should "include wait_for_completion when set" in {
    CancelTaskByIdHandler.build(CancelTaskByIdRequest("node1", "123").waitForCompletion(true)).params shouldBe
      Map("wait_for_completion" -> "true")
  }
}

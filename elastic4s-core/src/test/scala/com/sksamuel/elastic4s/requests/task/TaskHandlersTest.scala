package com.sksamuel.elastic4s.requests.task

import com.sksamuel.elastic4s.handlers.task.TaskHandlers
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.DurationInt

class TaskHandlersTest extends AnyFunSuite with Matchers with TaskHandlers {
  import com.sksamuel.elastic4s.ElasticApi._

  test("get task should not set any params by default") {
    val req = GetTaskHandler.build(getTask("node1", "123"))

    req.method shouldBe "GET"
    req.endpoint shouldBe "/_tasks/node1:123"
    req.params shouldBe Map.empty
  }

  test("get task should support wait_for_completion") {
    GetTaskHandler.build(getTask("node1", "123").waitForCompletion(true)).params shouldBe
      Map("wait_for_completion" -> "true")
    GetTaskHandler.build(getTask("node1", "123").waitForCompletion(false)).params shouldBe
      Map("wait_for_completion" -> "false")
  }

  test("get task should support timeout") {
    GetTaskHandler.build(getTask("node1", "123").timeout(30.seconds)).params shouldBe
      Map("timeout" -> "30000ms")
  }

  test("get task should support wait_for_completion and timeout together") {
    val req = GetTaskHandler.build(getTask("node1", "123").waitForCompletion(true).timeout(5.seconds))

    req.params shouldBe Map("wait_for_completion" -> "true", "timeout" -> "5000ms")
  }
}

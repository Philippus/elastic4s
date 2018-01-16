package com.sksamuel.elastic4s.tasks

import com.sksamuel.elastic4s.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class TasksTest extends FlatSpec with DockerTests with Matchers {

  "list tasks" should "include all fields" in {

    val resp = http.execute {
      listTasks()
    }.await.right.get.result

    resp.nodes.head._2.host shouldBe "127.0.0.1"
    resp.nodes.head._2.roles shouldBe Seq("master", "data", "ingest")
    resp.nodes.head._2.tasks.values.forall(_.startTime.toMillis > 0) shouldBe true
  }

}

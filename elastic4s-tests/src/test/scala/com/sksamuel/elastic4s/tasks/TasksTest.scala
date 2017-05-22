package com.sksamuel.elastic4s.tasks

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ClassloaderLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

class TasksTest extends FlatSpec with ClassloaderLocalNodeProvider with Matchers with ElasticDsl {

  "list tasks" should "include all fields" in {

    val resp = http.execute {
      listTasks()
    }.await

    resp.nodes.head._2.host shouldBe "127.0.0.1"
    resp.nodes.head._2.roles shouldBe Seq("master", "data", "ingest")
    resp.nodes.head._2.tasks.values.forall(_.startTime.toMillis > 0) shouldBe true
    resp.nodes.head._2.tasks.values.forall(_.runningTime.toMillis > 0) shouldBe true
  }

}

package com.sksamuel.elastic4s.tasks

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{FlatSpec, Matchers}

class TasksTest extends FlatSpec with SharedElasticSugar with Matchers with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "list tasks" should "include all fields" in {
    val resp = http.execute {
      listTasks()
    }.await
    resp.nodes.head._2.host shouldBe "local"
    resp.nodes.head._2.roles shouldBe Seq("master", "data", "ingest")
    resp.nodes.head._2.tasks.values.forall(_.startTime.toMillis > 0) shouldBe true
    resp.nodes.head._2.tasks.values.forall(_.runningTime.toMillis > 0) shouldBe true
  }

}

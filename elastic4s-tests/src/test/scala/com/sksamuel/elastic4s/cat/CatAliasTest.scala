package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class CatAliasTest extends FlatSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    indexInto("catalias/landmarks").fields("name" -> "hampton court palace").refresh(RefreshPolicy.IMMEDIATE)
  }.await

  http.execute {
    aliases(
      addAlias("ally1").on("catalias"),
      addAlias("ally2").on("catalias")
    )
  }.await


  "cats aliases" should "return all aliases" in {
    val result = http.execute {
      catAliases()
    }.await
    result.map(_.alias).toSet.contains("ally1") shouldBe true
    result.map(_.alias).toSet.contains("ally2") shouldBe true
  }
}

package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class CatAllocationTest extends FlatSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    bulk(
      indexInto("catalloc1/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catalloc2/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catalloc3/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await


  "cats alloc" should "return all shards" in {
    http.execute {
      catAllocation()
    }.await
  }

}

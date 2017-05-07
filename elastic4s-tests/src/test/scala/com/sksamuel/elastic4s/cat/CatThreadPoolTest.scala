package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class CatThreadPoolTest extends FlatSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    bulk(
      indexInto("amoonshapedpool1/landmarks").fields("name" -> "hampton court palace"),
      indexInto("amoonshapedpool2/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "cat thread pool" should "return all pools" in {
    http.execute {
      catThreadPool()
    }.await.map(_.name).toSet shouldBe Set("refresh", "bulk", "listener", "warmer", "generic", "fetch_shard_store", "snapshot", "force_merge", "management", "flush", "get", "fetch_shard_started", "index", "search")
  }

}

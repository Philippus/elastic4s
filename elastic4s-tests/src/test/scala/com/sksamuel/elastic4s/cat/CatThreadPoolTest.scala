package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

class CatThreadPoolTest extends FlatSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {

  http.execute {
    bulk(
      indexInto("amoonshapedpool1/landmarks").fields("name" -> "hampton court palace"),
      indexInto("amoonshapedpool2/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "cat thread pool" should "return all pools" in {
    http.execute {
      catThreadPool()
    }.await.map(_.name).toSet shouldBe Set("refresh", "bulk", "listener", "warmer", "generic", "fetch_shard_store", "snapshot", "force_merge", "management", "flush", "get", "fetch_shard_started", "index", "search")
  }

}

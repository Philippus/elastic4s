package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class CatThreadPoolTest extends FlatSpec with Matchers with DockerTests {

  client.execute {
    bulk(
      indexInto("amoonshapedpool1/landmarks").fields("name" -> "hampton court palace"),
      indexInto("amoonshapedpool2/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "cat thread pool" should "return all pools" in {
    val pools = client.execute {
      catThreadPool()
    }.await.result.map(_.name).toSet
    Set("refresh", "bulk", "listener", "warmer", "generic", "fetch_shard_store", "snapshot", "force_merge", "management", "flush", "get", "fetch_shard_started", "index", "search").foreach { pool =>
      pools.contains(pool) shouldBe true
    }
  }

}

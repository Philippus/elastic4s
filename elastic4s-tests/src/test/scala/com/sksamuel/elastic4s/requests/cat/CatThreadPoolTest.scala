package com.sksamuel.elastic4s.requests.cat

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
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

    Set("refresh", "listener", "warmer", "generic", "fetch_shard_store", "snapshot").foreach { pool =>
      pools.contains(pool) shouldBe true
    }
  }
}

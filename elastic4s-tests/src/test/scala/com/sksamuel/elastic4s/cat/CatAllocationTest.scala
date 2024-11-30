package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CatAllocationTest extends AnyFlatSpec with Matchers with DockerTests {

  client.execute {
    bulk(
      indexInto("catalloc1").fields("name" -> "hampton court palace"),
      indexInto("catalloc2").fields("name" -> "hampton court palace"),
      indexInto("catalloc3").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "cats alloc" should "return all shards" in {
    client.execute {
      catAllocation()
    }.await
  }

}

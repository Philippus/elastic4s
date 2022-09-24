package com.sksamuel.elastic4s.cluster

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NodeHotThreadsTest extends AnyFlatSpec with Matchers with DockerTests {

  client.execute {
    bulk(
      indexInto("hotthreads").fields("name" -> "hampton court palace"),
      indexInto("hotthreads").fields("name" -> "kensington palace"),
      indexInto("hotthreads").fields("name" -> "tower of london")
    ).refresh(RefreshPolicy.Immediate)
  }.await


  "nodeHotThreads" should "return all nodes" in {
    val result = client.execute {
      nodeHotThreads()
    }.await.result

    result should include ("Hot threads at")
    result should include ("ignoreIdleThreads")
    result should include ("interval")
    result should include ("busiestThreads")
  }
}

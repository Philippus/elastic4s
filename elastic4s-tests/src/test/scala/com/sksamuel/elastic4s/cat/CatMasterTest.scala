package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class CatMasterTest extends FlatSpec with Matchers with DockerTests {

  client.execute {
    bulk(
      indexInto("catmaster/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.Immediate)
  }.await


  "cat master" should "return master node info" in {
    val result = client.execute {
      catMaster()
    }.await.result

    result.host should not be null
    result.id should not be null
  }

}

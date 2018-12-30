package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class GetSegmentTest extends FlatSpec with Matchers with DockerTests {

  client.execute {
    bulk(
      indexInto("segments_1" / "a") fields ("show" -> "star trek"),
      indexInto("segments_2" / "b") fields ("show" -> "mindhunter"),
      indexInto("segments_3" / "c") fields ("show" -> "better call saul")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "getSegments" should "return segment list" in {
    val resp = client.execute {
      getSegments("segments_1", "segments_2", "segments_3")
    }.await
    val shards = resp.result.indices("segments_1")
    val segments = shards.shards.values.flatten.flatMap(_.segments.values).flatten
    segments.exists(_.search) shouldBe true
    segments.exists(_.sizeInBytes > 0) shouldBe true
    segments.exists(_.memoryInBytes > 0) shouldBe true
  }
}

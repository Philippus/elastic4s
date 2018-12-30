package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Inspectors, Matchers}

class CatSegmentsTest
  extends FlatSpec
    with Matchers
    with DockerTests
    with Inspectors {

  client.execute {
    bulk(
      indexInto("catseg/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catseg/landmarks").fields("name" -> "tower of london"),
      indexInto("catseg/landmarks").fields("name" -> "fountains abbey")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "cats segments" should "return all segments" in {
    val segments = client.execute {
      catSegments()
    }.await.result

    forAll(segments) { segment =>
      segment.ip should not be null
      segment.index should not be null
      segment.prirep should not be null
      segment.segment should not be null
      segment.generation should not be null
      segment.version should not be null
    }

    segments.map(_.size).sum > 0 shouldBe true
    segments.map(_.sizeMemory).sum > 0 shouldBe true
    segments.map(_.docsCount).sum  > 0 shouldBe true
  }

}

package com.sksamuel.elastic4s.locks

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, ElasticMatchers}
import org.scalatest.WordSpec

class LocksTest extends WordSpec
  with DiscoveryLocalNodeProvider
  with ElasticMatchers
  with ElasticDsl {

  "global lock" should {
    // todo are these going to be included in 6 or removed? Unsure at time of writing
    "only be acquired once" ignore {
      http.execute {
        acquireGlobalLock()
      }.await shouldBe true

      http.execute {
        acquireGlobalLock()
      }.await shouldBe false
    }

    // todo are these going to be included in 6 or removed? Unsure at time of writing
    "be releasable" ignore {
      http.execute {
        releaseGlobalLock()
      }.await shouldBe true
    }
  }
}

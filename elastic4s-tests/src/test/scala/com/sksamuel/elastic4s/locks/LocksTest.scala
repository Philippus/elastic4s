package com.sksamuel.elastic4s.locks

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.{ElasticMatchers, SharedElasticSugar}
import org.scalatest.WordSpec

class LocksTest extends WordSpec
  with SharedElasticSugar
  with ElasticMatchers
  with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "global lock" should {
    "only be acquired once" in {
      http.execute {
        acquireGlobalLock()
      }.await shouldBe true

      http.execute {
        acquireGlobalLock()
      }.await shouldBe false
    }

    "be releasable" in {
      http.execute {
        releaseGlobalLock()
      }.await shouldBe true
    }
  }
}

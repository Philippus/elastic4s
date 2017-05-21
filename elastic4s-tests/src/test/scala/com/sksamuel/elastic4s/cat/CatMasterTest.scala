package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ClassloaderLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

class CatMasterTest extends FlatSpec with Matchers with ClassloaderLocalNodeProvider with ElasticDsl {

  http.execute {
    bulk(
      indexInto("catmaster/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.Immediate)
  }.await


  "cat master" should "return master node info" in {
    http.execute {
      catMaster()
    }.await.host shouldBe "127.0.0.1"
  }

}

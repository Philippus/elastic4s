package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import org.scalatest.{Matchers, WordSpec}
import com.sksamuel.elastic4s.testkit.ElasticSugar

class TermVectorTest
  extends WordSpec
    with ElasticSugar
    with Matchers
    with ScalaFutures {

  override implicit def patienceConfig: PatienceConfig = PatienceConfig(timeout = 10.seconds, interval = 1.seconds)

  client.execute {
    bulk(
      index into "termvectortest/startrek" fields("name" -> "james kirk", "rank" -> "captain") id 1,
      index into "termvectortest/startrek" fields("name" -> "jean luc picard", "rank" -> "captain") id 2,
      index into "termvectortest/startrek" fields("name" -> "will riker", "rank" -> "cmdr") id 3,
      index into "termvectortest/startrek" fields("name" -> "data", "rank" -> "ltr cmdr") id 4,
      index into "termvectortest/startrek" fields("name" -> "geordie la forge", "rank" -> "ltr cmdr") id 5
    )
  }.await

  "term vector api " should {
    "return number of terms for a field in " in {

      val f = client.execute {
        termVectors("termvectortest", "startrek", "5")
          .termStatistics(true)
          .fields("name", "rank")
          .fieldStatistics(true)
      }

      whenReady(f) { resp =>
        resp.index shouldBe "termvectortest"
        resp.`type` shouldBe "startrek"
        resp.id shouldBe "5"
        resp.fields.terms("name").size shouldBe 3 // geordie la forge
        resp.fields.terms("rank").size shouldBe 2 // ltr cmdr
      }
    }
  }
}

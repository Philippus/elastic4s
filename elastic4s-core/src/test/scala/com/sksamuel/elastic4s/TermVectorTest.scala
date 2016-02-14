package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import org.scalatest.{Matchers, WordSpec}

/** @author Stephen Samuel */
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
        termVector("termvectortest", "startrek", "5")
          .withTermStatistics(true)
          .withFields("name", "rank")
          .withFieldStatistics(true)
      }

      whenReady(f) { resp =>
        val fields = resp.getFields
        val terms = fields.terms("rank").size shouldBe 2 // ltr cmdr
      }
    }
  }
}

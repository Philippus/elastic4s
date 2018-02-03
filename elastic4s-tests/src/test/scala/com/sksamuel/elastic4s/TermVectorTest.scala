package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import org.scalatest.{Matchers, WordSpec}

class TermVectorTest
  extends WordSpec
    with DockerTests
    with Matchers
    with ScalaFutures {

  override implicit def patienceConfig: PatienceConfig = PatienceConfig(timeout = 10.seconds, interval = 1.seconds)

  http.execute {
    bulk(
      indexInto("termvectortest/startrek") fields("name" -> "james kirk", "rank" -> "captain") id "1",
      indexInto("termvectortest/startrek") fields("name" -> "jean luc picard", "rank" -> "captain") id "2",
      indexInto("termvectortest/startrek") fields("name" -> "will riker", "rank" -> "cmdr") id "3",
      indexInto("termvectortest/startrek") fields("name" -> "data", "rank" -> "ltr cmdr") id "4",
      indexInto("termvectortest/startrek") fields("name" -> "geordie la forge", "rank" -> "ltr cmdr") id "5"
    )
  }.await

  "term vector api " should {
    "return number of terms for a field in " in {

      val f = http.execute {
        termVectors("termvectortest", "startrek", "5")
          .termStatistics(true)
          .fields("name", "rank")
          .fieldStatistics(true)
      }

      whenReady(f) { resp =>
        val result = resp.result
        result.index shouldBe "termvectortest"
        result.`type` shouldBe "startrek"
        result.id shouldBe "5"
        result.termVectors("name").terms.size shouldBe 3 // geordie la forge
        result.termVectors("rank").terms.size shouldBe 2 // ltr cmdr
      }
    }
  }
}

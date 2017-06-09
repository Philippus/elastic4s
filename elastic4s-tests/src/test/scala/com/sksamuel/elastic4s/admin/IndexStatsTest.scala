package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.testkit.{ElasticSugar, SharedElasticSugar}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import org.scalatest.{Matchers, WordSpec}

class IndexStatsTest extends WordSpec with Matchers with SharedElasticSugar with ScalaFutures {

  client.execute {
    bulk(
      indexInto("segments_movies" / "character") fields("name" -> "star trek", "show" -> "kirk"),
      indexInto("segments_tv" / "character") fields("name" -> "michael", "show" -> "knightrider"),
      indexInto("segments_theatre" / "character") fields("name" -> "glinda", "show" -> "wicked")
    )
  }

  blockUntilCount(1, "segments_movies")
  blockUntilCount(1, "segments_tv")
  blockUntilCount(1, "segments_theatre")

  override implicit def patienceConfig = PatienceConfig(timeout = 10.seconds, interval = 1.seconds)

  "indexStats(*)" should {
    "return all indexes" in {
      val f = client.execute {
        indexStats("*")
      }
      whenReady(f) { resp =>
        Set("segments_movies", "segments_tv", "segments_theatre").foreach { index =>
          resp.indexNames should contain(index)
        }
      }
    }
  }

  "indexStats(indexName)" should {
    "return stats for specified indexes" in {
      val f = client.execute {
        indexStats("segments_movies", "segments_tv")
      }
      whenReady(f) { resp =>
        resp.indexNames shouldBe Set("segments_movies", "segments_tv")
      }
    }
  }
}

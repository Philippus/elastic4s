package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{WordSpec, Matchers}
import org.scalatest.time.SpanSugar._

class SegmentsTest extends WordSpec with Matchers with ElasticSugar with ScalaFutures {

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

  "GetSegments(*)" should {
    "return segments for all indexes" in {
      val f = client.execute {
        getSegments("*")
      }
      whenReady(f) { resp =>
        Set("segments_movies", "segments_tv", "segments_theatre").foreach { indexName =>
          resp.indices.keys.toSet should contain(indexName)
        }
      }
    }
  }

  "GetSegments(indexName)" should {
    "return segments for specified indexes" in {
      val f = client.execute {
        getSegments("segments_movies", "segments_tv")
      }
      whenReady(f) { resp =>
        resp.indices.keys.toSet shouldBe Set("segments_movies", "segments_tv")
      }
    }
  }
}

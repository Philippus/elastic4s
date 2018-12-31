package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.concurrent.TimeLimits
import org.scalatest.exceptions.TestFailedDueToTimeoutException
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class OpenCloseIndexRequestTest extends WordSpec with Matchers with DockerTests with TimeLimits {

  Try {
    client.execute {
      deleteIndex("pasta")
    }.await
  }

  client.execute {
    createIndex("pasta").mappings(
      mapping("types").fields(
        textField("name"),
        textField("region")
      )
    )
  }.await

  "close index" should {
    "acknowledge" in {
      client.execute {
        closeIndex("pasta")
      }.await.result.acknowledged shouldBe true
    }
  }

  "open index" should {
    "acknowledge" in {
      client.execute {
        openIndex("pasta")
      }.await.result.acknowledged shouldBe true
    }
    "wait for active shards" in {

      client.execute {
        openIndex("pasta").waitForActiveShards(1)
      }.await.result.acknowledged shouldBe true

      // this should timeout as we don't have enough shards
      intercept[TestFailedDueToTimeoutException] {
        failAfter(Span(3, Seconds)) {
          client.execute {
            openIndex("pasta").waitForActiveShards(10)
          }.await
        }
      }
    }
    "support ignore unavailable" in {

      client.execute {
        openIndex("qeqweqwe")
      }.await.isError shouldBe true

      client.execute {
        openIndex("qweqewe").ignoreUnavailable(true)
      }.await.result.acknowledged shouldBe true
    }
  }

}

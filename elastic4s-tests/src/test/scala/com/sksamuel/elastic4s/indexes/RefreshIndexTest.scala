package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DualClientTests
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.util.Try

class RefreshIndexTest extends WordSpec with Matchers with ElasticDsl with DualClientTests {

  override protected def beforeRunTests(): Unit = {

    Try {
      execute {
        deleteIndex("beaches")
      }.await
    }

    execute {
      createIndex("beaches").mappings(
        mapping("dday").fields(
          textField("name")
        )
      ).shards(1).waitForActiveShards(1).refreshInterval(10.minutes)
    }.await

  }

  "refresh index request" should {
    "refresh pending docs" in {

      execute {
        indexInto("beaches" / "dday").fields("name" -> "omaha")
      }.await

      // no data because the refresh is 10 minutes
      execute {
        search("beaches" / "dday").matchAllQuery()
      }.await.totalHits shouldBe 0

      execute {
        refreshIndex("beaches")
      }.await

      execute {
        search("beaches" / "dday").matchAllQuery()
      }.await.totalHits shouldBe 1
    }
  }
}

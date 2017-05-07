package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class RefreshIndexTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  "refresh index request" should {
    "refresh pending docs" in {

      execute {
        createIndex("beaches").mappings(
          mapping("dday").fields(
            textField("name")
          )
        ).shards(1).waitForActiveShards(1).refreshInterval(10.minutes)
      }.await

      execute {
        indexInto("beaches" / "dday").fields("name" -> "omaha")
      }.await

      // no data will have been refreshed for 10 minutes
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

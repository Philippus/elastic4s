package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class FlushIndexTest extends WordSpec with Matchers with DualElasticSugar with DualClient {

  override protected def beforeRunTests(): Unit = {
    execute {
      createIndex("flushindex").mappings(
        mapping("pasta").fields(
          textField("name")
        )
      )
    }.await
  }

  "flush index" should {
    "acknowledge" in {
      execute {
        flushIndex("flushindex")
      }.await.shards.successful > 0 shouldBe true
    }
  }
}

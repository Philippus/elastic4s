package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DualClientTests
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class OpenCloseIndexTest extends WordSpec with Matchers with ElasticDsl  with DualClientTests {

  override protected def beforeRunTests(): Unit = {

    Try {
      execute {
        deleteIndex("pasta")
      }.await
    }

    execute {
      createIndex("pasta").mappings(
        mapping("types").fields(
          textField("name"),
          textField("region")
        )
      )
    }.await
  }

  "close index" should {
    "acknowledge" in {
      execute {
        closeIndex("pasta")
      }.await.acknowledged shouldBe true
    }
  }

  "open index" should {
    "acknowledge" in {
      execute {
        openIndex("pasta")
      }.await.acknowledged shouldBe true
    }
  }
}

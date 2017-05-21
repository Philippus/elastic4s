package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.testkit.DualClientTests
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.{Matchers, WordSpec}

class FlushIndexTest extends WordSpec with Matchers with DualClientTests {

  override protected def beforeRunTests(): Unit = {
    execute {
      createIndex(indexname).mappings(
        mapping("pasta").fields(
          textField("name")
        )
      )
    }.await
  }

  "flush index" should {
    "acknowledge" in {
      execute {
        flushIndex(indexname)
      }.await.shards.successful > 0 shouldBe true
    }
  }
}

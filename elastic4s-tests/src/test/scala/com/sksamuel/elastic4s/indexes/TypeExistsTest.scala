package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DualClientTests
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class TypeExistsTest extends WordSpec with Matchers with ElasticDsl with DualClientTests {

  override protected def beforeRunTests(): Unit = {

    Try {
      execute {
        deleteIndex("typeexists")
      }.await
    }

    execute {
      createIndex("typeexists").mappings {
        mapping("flowers") fields textField("name")
      }
    }.await
  }

  "a type exists request" should {
    "return true for an existing type" in {
      execute {
        typesExist("typeexists" / "flowers")
      }.await.isExists shouldBe true
    }
    "return false for non existing type" in {
      execute {
        typesExist("typeexists" / "qeqweqew")
      }.await.isExists shouldBe false
    }
  }
}

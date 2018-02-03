package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class TypeExistsTest extends WordSpec with Matchers with DockerTests {

  Try {
    http.execute {
      deleteIndex("typeexists")
    }.await
  }

  http.execute {
    createIndex("typeexists").mappings {
      mapping("flowers") fields textField("name")
    }
  }.await

  "a type exists request" should {
    "return true for an existing type" in {
      http.execute {
        typesExist("typeexists" / "flowers")
      }.await.result.isExists shouldBe true
    }
    "return false for non existing type" in {
      http.execute {
        typesExist("typeexists" / "qeqweqew")
      }.await.result.isExists shouldBe false
    }
  }
}

package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class RolloverIndexTest extends WordSpec with Matchers with DockerTests {

  Try {
    http.execute {
      deleteIndex("rolltest-001")
    }.await
  }

  Try {
    http.execute {
      deleteIndex("rolltest-000002")
    }.await
  }

  Try {
    http.execute {
      deleteIndex("rolltest-000003")
    }.await
  }

  Try {
    http.execute {
      aliases(
        removeAlias("roll_write", "rolltest-001")
      )
    }.await
  }

  Try {
    http.execute {
      aliases(
        removeAlias("roll_write", "rolltest-000002")
      )
    }.await
  }

  Try {
    http.execute {
      aliases(
        removeAlias("roll_write", "rolltest-000003")
      )
    }.await
  }

  Try {
    http.execute {
      aliases(
        removeAlias("roll_write", "rolltest-000004")
      )
    }.await
  }

  Try {
    http.execute {
      aliases(
        removeAlias("roll_write", "rolltest-000005")
      )
    }.await
  }

  http.execute {
    createIndex("rolltest-001").alias("roll_write")
  }.await

  "Rollover" should {
    "be created with padded index name" in {
      http.execute {
        rolloverIndex("roll_write")
      }.await.right.get.result.newIndex shouldBe "rolltest-000002"
    }
    "support dry run" in {
      val resp = http.execute {
        rolloverIndex("roll_write").maxAge("1d").dryRun(true)
      }.await
      val result = resp.right.get.result
      result.dryRun shouldBe true
      result.rolledOver shouldBe false
    }
    "return conditions in response" in {
      http.execute {
        rolloverIndex("roll_write").maxDocs(10).maxSize("5g")
      }.await.right.get.result.conditions shouldBe Map("[max_docs: 10]" -> false, "[max_size: 5gb]" -> false)
    }
    "support max docs" in {
      http.execute {
        bulk(
          indexInto("roll_write" / "wibble").fields("foo" -> "woo"),
          indexInto("roll_write" / "wibble").fields("foo" -> "woo"),
          indexInto("roll_write" / "wibble").fields("foo" -> "woo"),
          indexInto("roll_write" / "wibble").fields("foo" -> "woo"),
          indexInto("roll_write" / "wibble").fields("foo" -> "woo"),
          indexInto("roll_write" / "wibble").fields("foo" -> "woo"),
          indexInto("roll_write" / "wibble").fields("foo" -> "woo"),
          indexInto("roll_write" / "wibble").fields("foo" -> "woo"),
          indexInto("roll_write" / "wibble").fields("foo" -> "woo"),
          indexInto("roll_write" / "wibble").fields("foo" -> "woo")
        ).refreshImmediately
      }.await
      http.execute {
        search("rolltest-000002").limit(20)
      }.await.right.get.result.totalHits shouldBe 10

      val resp = http.execute {
        rolloverIndex("roll_write").maxDocs(10)
      }.await.right.get.result
      resp.conditions shouldBe Map("[max_docs: 10]" -> true)
      resp.rolledOver shouldBe true
      resp.newIndex shouldBe "rolltest-000003"
    }
  }
}

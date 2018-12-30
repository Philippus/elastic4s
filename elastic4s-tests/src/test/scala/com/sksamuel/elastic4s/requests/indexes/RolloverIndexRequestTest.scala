package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class RolloverIndexRequestTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("rolltest-001")
    }.await
  }

  Try {
    client.execute {
      deleteIndex("rolltest-000002")
    }.await
  }

  Try {
    client.execute {
      deleteIndex("rolltest-000003")
    }.await
  }

  Try {
    client.execute {
      aliases(
        removeAlias("roll_write", "rolltest-001")
      )
    }.await
  }

  Try {
    client.execute {
      aliases(
        removeAlias("roll_write", "rolltest-000002")
      )
    }.await
  }

  Try {
    client.execute {
      aliases(
        removeAlias("roll_write", "rolltest-000003")
      )
    }.await
  }

  Try {
    client.execute {
      aliases(
        removeAlias("roll_write", "rolltest-000004")
      )
    }.await
  }

  Try {
    client.execute {
      aliases(
        removeAlias("roll_write", "rolltest-000005")
      )
    }.await
  }

  client.execute {
    createIndex("rolltest-001").alias("roll_write")
  }.await

  "Rollover" should {
    "be created with padded index name" in {
      client.execute {
        rolloverIndex("roll_write")
      }.await.result.newIndex shouldBe "rolltest-000002"
    }
    "support dry run" in {
      val resp = client.execute {
        rolloverIndex("roll_write").maxAge("1d").dryRun(true)
      }.await
      val result = resp.result
      result.dryRun shouldBe true
      result.rolledOver shouldBe false
    }
    "return conditions in response" in {
      client.execute {
        rolloverIndex("roll_write").maxDocs(10).maxSize("5g")
      }.await.result.conditions shouldBe Map("[max_docs: 10]" -> false, "[max_size: 5gb]" -> false)
    }
    "support max docs" in {
      client.execute {
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
      client.execute {
        search("rolltest-000002").limit(20)
      }.await.result.totalHits shouldBe 10

      val resp = client.execute {
        rolloverIndex("roll_write").maxDocs(10)
      }.await.result
      resp.conditions shouldBe Map("[max_docs: 10]" -> true)
      resp.rolledOver shouldBe true
      resp.newIndex shouldBe "rolltest-000003"
    }
  }
}

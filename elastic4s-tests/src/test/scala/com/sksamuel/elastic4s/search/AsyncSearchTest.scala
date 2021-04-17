package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.concurrent.Eventually
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class AsyncSearchTest extends AnyFlatSpec with Matchers with Eventually with DockerTests {

  Try {
    client.execute {
      ElasticDsl.deleteIndex("colors")
    }.await
  }

  client.execute {
    createIndex("colors").mapping(
      properties(
        textField("name").fielddata(true)
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("colors").fields("name" -> "green").id("1"),
      indexInto("colors").fields("name" -> "blue").id("2"),
      indexInto("colors").fields("name" -> "red").id("3")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "an async search query" should "find an indexed document that matches a term query immediately" in {
    val asyncSearch = client.execute {
      search("colors") query termQuery("name", "green") async()
    }.await.result

    asyncSearch.id shouldBe None
    asyncSearch.response.totalHits shouldBe 1
  }

  it should "find an indexed document that matches a term query asynchronously and then delete the search" in {
    val asyncSearch = client.execute {
      search("colors") query termQuery("name", "red") async() keepOnCompletion(true)
    }.await.result

    val asyncSearchId = asyncSearch.id.getOrElse(fail("Id not found in async search"))

    eventually {
      client.execute {
        fetchAsyncSearch(asyncSearchId)
      }.await.result.response.totalHits shouldBe 1
    }

    val deleteResult = client.execute {
      clearAsyncSearch(asyncSearchId)
    }.await.result.acknowledged
    deleteResult shouldBe true
  }

  it should "fetch status" in {
    val asyncSearch = client.execute {
      search("colors") query termQuery("name", "blue") async() keepOnCompletion(true)
    }.await.result

    val asyncSearchId = asyncSearch.id.getOrElse(fail("Id not found in async search"))

    val isCompleted = client.execute {
      asyncSearchStatus(asyncSearchId)
    }.await.result.completionStatus

    isCompleted shouldBe Some(200)
  }
}

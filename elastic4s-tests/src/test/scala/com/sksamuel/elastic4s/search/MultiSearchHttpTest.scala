package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.SearchType
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class MultiSearchHttpTest
  extends FlatSpec
    with DockerTests
    with Matchers {

  Try {
    client.execute {
      deleteIndex("jtull")
    }.await
  }

  client.execute {
    createIndex("jtull")
  }.await

  client.execute {
    bulk(
      indexInto("jtull" / "albums") fields ("name" -> "aqualung") id "14",
      indexInto("jtull" / "albums") fields ("name" -> "passion play") id "51"
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a multi search request" should "perform search for all queries" in {

    val resp = client.execute {
      multi(
        search("jtull") query matchQuery("name", "aqualung"),
        search("jtull") query "passion",
        search("jtull" / "albums") query matchAllQuery()
      )
    }.await.result

    resp.successes.size shouldBe 3
    resp.size shouldBe 3

    resp.successes.head.hits.hits.head.id shouldBe "14"
    resp.successes.tail.head.hits.hits.head.id shouldBe "51"

    resp.successes.head.totalHits shouldBe 1
    resp.successes.tail.head.totalHits shouldBe 1
    resp.successes.last.totalHits shouldBe 2
  }

  it should "correctly set errored and successful items" in {

    val resp = client.execute {
      multi(
        search("jtull") query matchQuery("name", "aqualung"),
        search("unknown") query matchAllQuery()
      )
    }.await.result

    resp.successes.size shouldBe 1
    resp.failures.size shouldBe 1
    resp.items.head.index shouldBe 0
    resp.items.head.status shouldBe 200
    resp.items.last.index shouldBe 1
    resp.items.last.status shouldBe 404

    resp.failures.head.`reason` shouldBe "no such index [unknown]"
    resp.failures.head.`type` shouldBe "index_not_found_exception"
  }

  it should "correctly set the preference" in {

    val request = MultiSearchHandler.build(
      multi(
        search("jtull") query matchQuery("name", "aqualung") preference "first_preference",
        search("unknown") query matchAllQuery() preference "second_preference"
      )
    )

    val expectedEntity =
      """{"index":"jtull","preference":"first_preference"}
        |{"query":{"match":{"name":{"query":"aqualung"}}}}
        |{"index":"unknown","preference":"second_preference"}
        |{"query":{"match_all":{}}}
        |""".stripMargin

    request.entity.head.get shouldBe expectedEntity
  }

  it should "correctly set the search type" in {

    val request = MultiSearchHandler.build(
      multi(
        search("jtull") query matchQuery("name", "aqualung") searchType SearchType.DFS_QUERY_THEN_FETCH,
        search("unknown") query matchAllQuery()
      )
    )


    val expectedEntity =
      """{"index":"jtull","search_type":"dfs_query_then_fetch"}
        |{"query":{"match":{"name":{"query":"aqualung"}}}}
        |{"index":"unknown"}
        |{"query":{"match_all":{}}}
        |""".stripMargin

    request.entity.head.get shouldBe expectedEntity
  }
}

package com.sksamuel.elastic4s.aliases

import com.sksamuel.elastic4s.http.index.admin.{AliasActionResponse, AliasExistsResponse}
import com.sksamuel.elastic4s.http.index.alias.{Alias, IndexAliases}
import com.sksamuel.elastic4s.{DockerTests, Index}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class AliasesHttpTest extends WordSpec with Matchers with DockerTests {

  removeIndex("beaches")
  removeIndex("mountains")

  addIndex("beaches")
  addIndex("mountains")

  http.execute {
    indexInto("beaches/a").fields("name" -> "gold").refreshImmediately
  }.await

  http.execute {
    indexInto("beaches/a").fields("name" -> "sword").refreshImmediately
  }.await

  "alias actions" should {
    "support adding an alias" in {
      http.execute {
        aliases(
          addAlias("beaches_alias", "beaches")
        )
      }.await.right.get.result shouldBe AliasActionResponse(true)

      http.execute {
        getAliases(Nil, Seq("beaches_alias"))
      }.await.right.get.result shouldBe IndexAliases(Map(Index("beaches") -> List(Alias("beaches_alias"))))
    }
    "multiple operations" in {
      http.execute {
        aliases(
          removeAlias("beaches_alias").on("beaches"),
          addAlias("mountains_alias").on("mountains")
        )
      }.await.right.get.result should be(AliasActionResponse(true))

      http.execute {
        aliasExists("mountains_alias")
      }.await.right.get.result should be(AliasExistsResponse(true))

      http.execute {
        aliasExists("beaches_alias")
      }.await.right.get.result should be(AliasExistsResponse(false))

      http.execute {
        getAliases(Nil, Seq("mountains_alias"))
      }.await.right.get.result shouldBe IndexAliases(Map(Index("mountains") -> List(Alias("mountains_alias"))))

      http.execute {
        getAliases(Nil, Seq("beaches_alias"))
      }.await.right.get.result shouldBe IndexAliases(Map.empty)
    }
    "support removing an alias" in {
      http.execute {
        aliases(
          removeAlias("mountains_alias", "mountains")
        )
      }.await.right.get.result should be(AliasActionResponse(true))

      http.execute {
        aliasExists("mountains_alias")
      }.await.right.get.result should be(AliasExistsResponse(false))

      http.execute {
        getAliases(Nil, Seq("mountains_alias"))
      }.await.right.get.result shouldBe IndexAliases(Map.empty)
    }
  }

  "getAliases" should {
    "return empty response when the index is not found" in {
      http.execute {
        getAliases("does_not_exist", Nil)
      }.await.right.get.result shouldBe IndexAliases(Map.empty)
    }
    "return empty response when no index of many is found" in {
      http.execute {
        aliases(
          addAlias("beaches_alias", "beaches")
        )
      }.await.right.get.result shouldBe AliasActionResponse(true)

      http.execute {
        getAliases(Seq("does_not_exist", "beaches"), Nil)
      }.await.right.get.result shouldBe IndexAliases(Map())
    }
    "support multiple indexes where many are found" in {
      http.execute {
        getAliases(Seq("mountains", "beaches"), Nil)
      }.await.right.get.result shouldBe IndexAliases(Map(Index("beaches") -> List(Alias("beaches_alias")), Index("mountains") -> Nil))
    }
    "return all aliases / all indexes when nothing is specified" in {

      http.execute {
        aliases(
          addAlias("sandy_beaches", "beaches"),
          addAlias("big_mountains", "mountains")
        )
      }.await.right.get.result shouldBe AliasActionResponse(true)

      val results = http.execute {
        getAliases()
      }.await.right.get.result

      val map = results.mappings
      map(Index("beaches")) shouldBe List(Alias("beaches_alias"), Alias("sandy_beaches"))
      map(Index("mountains")) shouldBe List(Alias("big_mountains"))
    }
  }

  "alias filters" should {
    "filter search results" in {
      http.execute {
        aliases(
          addAlias("metal_beaches", "beaches").filter(prefixQuery("name", "g"))
        )
      }.await.right.get.result shouldBe AliasActionResponse(true)

      val result = http.execute {
        search("metal_beaches").matchAllQuery()
      }.await.right.get.result
      result.hits.hits.length shouldBe 1
      result.hits.hits.head.sourceAsMap("name") shouldBe "gold"
    }
  }

  private def removeIndex(name: String): Unit = {
    Try {
      http.execute {
        deleteIndex(name)
      }.await
    }
  }

  private def addIndex(index: String): Unit = {
    http.execute {
      createIndex(index).mappings(
        mapping("a").fields(
          textField("b")
        )
      ).shards(1).replicas(0).waitForActiveShards(1)
    }.await
  }
}

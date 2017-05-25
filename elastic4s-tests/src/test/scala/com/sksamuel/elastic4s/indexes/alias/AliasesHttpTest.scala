package com.sksamuel.elastic4s.indexes.alias

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.index.admin.{AliasExistsResponse, IndicesAliasResponse}
import com.sksamuel.elastic4s.http.index.alias.Alias
import com.sksamuel.elastic4s.testkit.ClassloaderLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class AliasesHttpTest extends WordSpec with Matchers with ClassloaderLocalNodeProvider with ElasticDsl {

  removeIndex("beaches")
  removeIndex("mountains")

  addIndex("beaches")
  addIndex("mountains")

  "alias actions" should {
    "be executed" in {
      http.execute {
        aliases(
          addAlias("landscapes").on("beaches")
        )
      }.await shouldBe IndicesAliasResponse(true)

      http.execute {
        aliases(
          addAlias("landscapes").on("mountains"),
          removeAlias("landscapes").on("beaches")
        )
      }.await should be(IndicesAliasResponse(true))

      http.execute {
        aliasExists("landscapes")
      }.await should be(AliasExistsResponse(true))

      http.execute {
        getAlias("landscapes")
      }.await shouldBe Map("mountains" -> Map("aliases" -> Map("landscapes" -> Map())))

    }

    "return empty response when not found" in {
      http.execute {
        getAlias("does_not_exist")
      }.await shouldBe Map()
    }
  }

  "get aliases" should {
    "return all aliases" in {
      val aliases = http.execute {
        getAliases()
      }.await.toSet

      aliases.contains(Alias("mountains", Vector("landscapes"))) shouldBe true
      aliases.contains(Alias("beaches", Nil)) shouldBe true
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
        mapping("dday").fields(
          textField("name")
        )
      ).shards(1).replicas(0).waitForActiveShards(1)
    }.await
  }
}

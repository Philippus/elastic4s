package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.index.IndexShardStoreResponse.{IndexStoreStatus, ShardStoreStatus}
import com.sksamuel.elastic4s.http.index.{AliasExistsResponse, IndicesAliasResponse}
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{Matchers, WordSpec}

class AliasActionHttpTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "alias actions" should {
    "executed" in {
      addIndex("beaches")
      addIndex("mountains")

      http.execute {
        aliases(
          addAlias("landscapes").on("beaches")
        )
      }.await should be(IndicesAliasResponse(true))

      http.execute {
        aliases(
          addAlias("landscapes").on("mountains"),
          removeAlias("landscapes").on("beaches")
        )
      }.await should be(IndicesAliasResponse(true))

      http.execute {
        aliasExists("landscapes")
      }.await should be(AliasExistsResponse(true))
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

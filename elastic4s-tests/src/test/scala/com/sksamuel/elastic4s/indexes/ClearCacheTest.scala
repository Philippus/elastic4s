package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ClearCacheTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  Try {
    http.execute {
      deleteIndex("clearcache1")
    }.await
  }

  Try {
    http.execute {
      deleteIndex("clearcache2")
    }.await
  }

  http.execute {
    createIndex("clearcache1").mappings(
      mapping("flowers").fields(
        textField("name")
      )
    )
  }.await

  http.execute {
    createIndex("clearcache2").mappings(
      mapping("plants").fields(
        textField("name")
      )
    )
  }.await

  "ClearCache" should {
    "support single index" in {
      val resp = http.execute {
        clearCache("clearcache1")
      }.await
      resp.get.shards.successful should be > 0
    }

    "support multiple types" in {
      val resp = http.execute {
        clearCache("clearcache1", "clearcache2")
      }.await
      resp.get.shards.successful should be > 0
    }
  }
}

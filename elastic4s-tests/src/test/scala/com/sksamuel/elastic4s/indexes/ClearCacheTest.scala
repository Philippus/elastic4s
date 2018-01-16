package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.DockerTests
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ClearCacheTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("clearcache1")
    }.await
  }

  Try {
    client.execute {
      deleteIndex("clearcache2")
    }.await
  }

  client.execute {
    createIndex("clearcache1").mappings(
      mapping("flowers").fields(
        textField("name")
      )
    )
  }.await

  client.execute {
    createIndex("clearcache2").mappings(
      mapping("plants").fields(
        textField("name")
      )
    )
  }.await

  "ClearCache" should {
    "support single index" in {
      val resp = client.execute {
        clearCache("clearcache1")
      }.await
      resp.right.get.result.shards.successful should be > 0
    }

    "support multiple types" in {
      val resp = client.execute {
        clearCache("clearcache1", "clearcache2")
      }.await
      resp.right.get.result.shards.successful should be > 0
    }
  }
}

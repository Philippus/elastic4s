package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class ClearCacheTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  override protected def beforeRunTests() = {
    execute {
      createIndex("clearcache1").mappings(
        mapping("flowers").fields(
          textField("name")
        )
      )
    }.await

    execute {
      createIndex("clearcache2").mappings(
        mapping("plants").fields(
          textField("name")
        )
      )
    }.await
  }

  "ClearCache" should {
    "support single index" in {
      val resp = execute {
        clearCache("clearcache1")
      }.await
      resp.shards.successful should be > 0
    }

    "support multiple types" in {
      val resp = execute {
        clearCache("clearcache1", "clearcache2")
      }.await
      resp.shards.successful should be > 0
    }
  }
}

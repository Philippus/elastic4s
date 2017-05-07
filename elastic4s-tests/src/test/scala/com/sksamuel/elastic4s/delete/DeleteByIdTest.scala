package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

class DeleteByIdTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  "delete by id request" should {
    "delete matched docs" in {

      execute {
        createIndex("lecarre").mappings(
          mapping("characters").fields(
            textField("name")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      execute {
        indexInto("lecarre" / "characters").fields("name" -> "jonathon pine").id(2).refresh(RefreshPolicy.IMMEDIATE)
      }.await

      execute {
        indexInto("lecarre" / "characters").fields("name" -> "george smiley").id(4).refresh(RefreshPolicy.IMMEDIATE)
      }.await

      execute {
        search("lecarre" / "characters").matchAllQuery()
      }.await.totalHits shouldBe 2

      execute {
        delete(2).from("lecarre" / "characters").refresh(RefreshPolicy.IMMEDIATE)
      }.await

      execute {
        search("lecarre" / "characters").matchAllQuery()
      }.await.totalHits shouldBe 1

      execute {
        delete(4).from("lecarre" / "characters").refresh(RefreshPolicy.IMMEDIATE)
      }.await

      execute {
        search("lecarre" / "characters").matchAllQuery()
      }.await.totalHits shouldBe 0
    }
  }
}

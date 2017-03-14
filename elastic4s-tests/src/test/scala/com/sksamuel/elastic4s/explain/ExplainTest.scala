package com.sksamuel.elastic4s.explain

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class ExplainTest extends FlatSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  override protected def beforeRunTests() = {
    execute {
      bulk(
        indexInto("explain/kings") fields ("name" -> "richard") id 4,
        indexInto("explain/kings") fields ("name" -> "edward") id 5
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await
  }

  "an explain request" should "explain a matching document" in {
    val resp = execute {
      explain("explain", "kings", "4") query termQuery("name", "richard")
    }.await

    resp.isMatch shouldBe true
    resp.explanation.details.head.description shouldBe "weight(name:richard in 0) [PerFieldSimilarity], result of:"
  }

  it should "not explain a not found document" in {
    execute {
      explain("explain", "kings", "24") query termQuery("name", "edward")
    }.await.isMatch shouldBe false
  }
}

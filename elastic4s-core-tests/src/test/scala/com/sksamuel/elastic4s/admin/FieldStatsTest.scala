package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.ElasticDsl2$
import org.elasticsearch.action.fieldstats.FieldStats
import org.scalatest.{WordSpec, Matchers}
import com.sksamuel.elastic4s.testkit.ElasticSugar

class FieldStatsTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl2._

  client.execute(
    bulk(
      index into "fieldstats/pizza" fields("topping" -> "chicken", "qty" -> 56),
      index into "fieldstats/pizza" fields("topping" -> "pepperoni", "qty" -> 32),
      index into "fieldstats/pizza" fields("topping" -> "capers", "qty" -> 8),
      index into "fieldstats/pizza" fields("topping" -> "onions", "qty" -> 16),
      index into "fieldstats/pizza" fields("topping" -> "mushrooms", "qty" -> 73),
      index into "fieldstats/pizza" fields("topping" -> "sausage", "qty" -> 15),
      index into "fieldstats/pizza" fields("topping" -> "ham", "qty" -> 6)
    )
  ).await

  refresh("fieldstats")
  blockUntilCount(7, "fieldstats")

  "field stats" should {
    "return stats for specified fields" in {
      val resp = client.execute {
        field.stats("qty")
      }.await
      resp.fieldStats.size shouldBe 1
      resp.fieldStats.keySet shouldBe Set("qty")
    }
    "support Long stats" in {
      val resp = client.execute {
        field.stats("qty")
      }.await
      resp.fieldStats("qty") match {
        case text: FieldStats.Long =>
          text.getMaxValueAsString shouldBe "73"
          text.getMaxValue shouldBe 73
          text.getMinValueAsString shouldBe "6"
          text.getMinValue shouldBe 6
          text.getDocCount shouldBe 7
          text.getMaxDoc shouldBe 7
        case other => throw new UnsupportedOperationException(other.toString)
      }
    }
    "support Text stats" in {
      val resp = client.execute {
        field.stats("topping")
      }.await
      resp.fieldStats("topping") match {
        case text: FieldStats.Text => text.getDocCount shouldBe 7
        case other => throw new UnsupportedOperationException(other.toString)
      }
    }
  }
}

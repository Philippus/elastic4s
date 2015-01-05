package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{GeoPointType, IntegerType}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

/** @author Stephen Samuel */
class IndexTemplateTest extends FlatSpec with MockitoSugar with ElasticSugar with Matchers {

  "create template" should "be applied" in {

    client.execute {
      create template "brewery_template" pattern "*" mappings (
        mapping name "brewery" as(
          "year_founded" typed IntegerType,
          "location" typed GeoPointType
          )
        )
    }.await

    client.execute {
      index into "test" / "brewery" fields(
        "year_founded" -> 1829,
        "location" -> "33.9253, 18.4239"
        )
    }.await

    blockUntilCount(1, "test", "brewery")

    client.execute {
      search in "test" / "brewery" query termQuery("year_founded", 1829)
    }.await.getHits.totalHits shouldBe 1
  }
}

package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.FieldType.{GeoPointType, DateType}

/** @author Stephen Samuel */
class PutMappingTest extends FlatSpec with MockitoSugar with ElasticSugar {

  "a put mapping" should "be accepted by the client" in {
    client.putMapping("index1", "index2") {
      "tweets" as(
        "name" typed GeoPointType analyzer SimpleAnalyzer boost 4 index "not_analyzed",
        "content" typed DateType analyzer StopAnalyzer nullValue "no content"
        )
    }
  }
}

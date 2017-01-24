package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.mappings.FieldType.{DateType, GeoPointType}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.FlatSpec
import org.scalatest.mockito.MockitoSugar

class MappingApiTest extends FlatSpec with MockitoSugar with SharedElasticSugar {

  "a put mapping dsl" should "be accepted by the client" in {
    client.execute {
      put mapping "index" / "type" as Seq(
        field name "name" withType GeoPointType,
        field name "content" typed DateType nullValue "no content"
      )
    }
  }

  it should "accept same fields as mapping api" in {
    put mapping "index" / "type" as {
      field name "content" typed DateType nullValue "no content"
    } dynamic DynamicMapping.False numericDetection true boostNullValue 12.2 boost "boosty"
  }

  "the get mapping dsl" should "be accepted by the client" in {
    client.execute {
      get mapping "index" types "type"
    }
  }

  it should "support multiple indexes" in {
    client.execute {
      get mapping("index1", "index2")
    }
  }

  it should "support multiple types" in {
    client.execute {
      get mapping "index" types("type1", "type2")
    }
  }

  it should "support multiple indexes and multiple types" in {
    client.execute {
      get mapping("index1", "index2") types("type1", "type2")
    }
  }
}

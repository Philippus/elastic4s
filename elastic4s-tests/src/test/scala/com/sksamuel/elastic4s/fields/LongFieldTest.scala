package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.handlers.fields.ElasticFieldBuilderFn
import com.sksamuel.elastic4s.jackson.JacksonSupport
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class LongFieldTest extends AnyFunSuite with Matchers {

  test("LongField fields") {
    val field = LongField(
      name = "myfield",
      store = Some(true),
      coerce = Some(true),
      boost = Some(1.2),
      nullValue = Some(142),
      ignoreMalformed = Some(true),
      index = Some(true),
      copyTo = List("q", "er")
    )

    val jsonStringValue = """{"type":"long","copy_to":["q","er"],"boost":1.2,"index":true,"null_value":142,"store":true,"coerce":true,"ignore_malformed":true}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
    ElasticFieldBuilderFn.construct(field.name, JacksonSupport.mapper.readValue[Map[String, Any]](jsonStringValue)) shouldBe (field)
  }
}

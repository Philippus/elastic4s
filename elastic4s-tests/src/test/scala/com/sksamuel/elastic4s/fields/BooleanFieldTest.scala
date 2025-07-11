package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.handlers.fields.ElasticFieldBuilderFn
import com.sksamuel.elastic4s.jackson.JacksonSupport
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class BooleanFieldTest extends AnyFunSuite with Matchers {
  test("Boolean fields") {
    val field = BooleanField(
      name = "myfield",
      store = Some(true),
      boost = Some(1.2),
      nullValue = Some(false),
      index = Some(true),
      copyTo = List("q", "er"),
      meta = Map("banana" -> "yellow", "strawberry" -> "red")
    )

    val jsonStringValue =
      """{"type":"boolean","boost":1.2,"copy_to":["q","er"],"index":true,"null_value":false,"store":true,"meta":{"banana":"yellow","strawberry":"red"}}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
    ElasticFieldBuilderFn.construct(
      field.name,
      JacksonSupport.mapper.readValue[Map[String, Any]](jsonStringValue)
    ) shouldBe (field)
  }
}

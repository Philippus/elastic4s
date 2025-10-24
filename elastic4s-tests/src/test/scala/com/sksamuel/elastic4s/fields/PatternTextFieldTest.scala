package com.sksamuel.elastic4s.fields

import com.sksamuel.elastic4s.handlers.fields.ElasticFieldBuilderFn
import com.sksamuel.elastic4s.jackson.JacksonSupport
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class PatternTextFieldTest extends AnyFunSuite with Matchers {
  test("Pattern Text fields") {
    val field = PatternTextField(
      name = "myfield",
      analyzer = Some("my_analyzer"),
      indexOptions = Some("docs"),
      meta = Map("banana" -> "yellow", "strawberry" -> "red")
    )

    val jsonStringValue =
      """{"type":"pattern_text","analyzer":"my_analyzer","index_options":"docs","meta":{"banana":"yellow","strawberry":"red"}}"""
    ElasticFieldBuilderFn(field).string shouldBe jsonStringValue
    ElasticFieldBuilderFn.construct(
      field.name,
      JacksonSupport.mapper.readValue[Map[String, Any]](jsonStringValue)
    ) shouldBe (field)
  }
}

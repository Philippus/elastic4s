package com.sksamuel.elastic4s.requests.index.analyze

import com.sksamuel.elastic4s.analysis.StopAnalyzer
import com.sksamuel.elastic4s.handlers.index
import com.sksamuel.elastic4s.handlers.index.AnalyseRequestContentBuilder
import com.sksamuel.elastic4s.requests.indexes.analyze.AnalyzeRequest
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

class AnalyseRequestContentBuilderTest extends AnyFunSuite with Matchers {

  test("create analyse request content") {
    val content = AnalyseRequestContentBuilder(
      AnalyzeRequest(Array("hello world"))
        .analyzer("smartcn")
    )
    content mustBe
      """{"text":["hello world"],"analyzer":"smartcn"}""".stripMargin
  }

  test("create analyse request content with explain") {
    val content = index.AnalyseRequestContentBuilder(
      AnalyzeRequest(Array("hello world"))
        .analyzer("smartcn")
        .explain(true)
    )
    content mustBe
      """{"text":["hello world"],"analyzer":"smartcn","explain":true}""".stripMargin
  }

  test("create analyse request content with tokenizer") {
    val content = index.AnalyseRequestContentBuilder(
      AnalyzeRequest(Array("hello world"))
        .explain(true)
        .tokenizer("keyword")
    )
    content mustBe
      """{"text":["hello world"],"explain":true,"tokenizer":"keyword"}""".stripMargin
  }

  test("create analyse request content with tokenizer,filters,charFilters  ") {
    val content = index.AnalyseRequestContentBuilder(
      AnalyzeRequest(Array("hello world"))
        .tokenizer("keyword")
        .filters("lowercase")
        .charFilters("html_strip")
    )
    content mustBe
      """{"text":["hello world"],"tokenizer":"keyword","filter":["lowercase"],"char_filter":["html_strip"]}""".stripMargin
  }

  test("create analyse request content with custom filters  ") {
    val content = index.AnalyseRequestContentBuilder(
      AnalyzeRequest(Array("hello world"))
        .tokenizer("keyword")
        .filters("lowercase","uppercase")
        .filters(StopAnalyzer("stop",List("a","is","this")))
    )
    content mustBe
      """{"text":["hello world"],"tokenizer":"keyword","filter":["lowercase","uppercase",{"type":"stop","stopwords":["a","is","this"]}]}""".stripMargin
  }

  test("create analyse request content with normalizer") {
    val content = index.AnalyseRequestContentBuilder(
      AnalyzeRequest(Array("hello world"))
        .normalizer("my_normalizer")
    )
    content mustBe
      """{"text":["hello world"],"normalizer":"my_normalizer"}""".stripMargin
  }

  test("create analyse request content with field") {
    val content = index.AnalyseRequestContentBuilder(
      AnalyzeRequest(Array("hello world"))
        .field("obj1.field1")
    )
    content mustBe
      """{"text":["hello world"],"field":"obj1.field1"}""".stripMargin
  }

  test("create analyse request content with attributes") {
    val content = index.AnalyseRequestContentBuilder(
      AnalyzeRequest(Array("hello world"))
        .attributes("keyword")
    )
    content mustBe
      """{"text":["hello world"],"attributes":["keyword"]}""".stripMargin
  }

}

package com.sksamuel.elastic4s.requests.index.analyze

import com.sksamuel.elastic4s.requests.indexes.analyze.{AnalyseRequestContentBuilder, AnalyzeRequest}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

class AnalyseRequestContentBuilderTest extends AnyFunSuite with Matchers {

  test("create analyse request content") {
    val content = AnalyseRequestContentBuilder(
      AnalyzeRequest(Array("hello world"))
        .analyzer("smartcn")
    )
    println(content)
    content mustBe
      """{"text":["hello world"],"analyzer":"smartcn"}""".stripMargin
  }

  test("create analyse request content with explain") {
    val content = AnalyseRequestContentBuilder(
      AnalyzeRequest(Array("hello world"))
        .analyzer("smartcn")
        .explain(true)
    )
    println(content)
    content mustBe
      """{"text":["hello world"],"analyzer":"smartcn","explain":true}""".stripMargin
  }

}

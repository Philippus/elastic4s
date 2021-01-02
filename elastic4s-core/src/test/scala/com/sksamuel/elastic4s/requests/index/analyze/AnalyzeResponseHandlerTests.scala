package com.sksamuel.elastic4s.requests.index.analyze

import com.sksamuel.elastic4s.requests.indexes.analyze.AnalyzeResponseHandler
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

class AnalyzeResponseHandlerTests extends AnyFunSuite with Matchers {


  test("analyze response handler parse not explain response") {
    val result = AnalyzeResponseHandler.handle(create200HttpResponse(noExplainResponseJson)).getOrElse(null)
    result mustNot be(null)
    result mustBe noExplainResponse
  }

  test("analyze response handler parse explain response") {
    val result = AnalyzeResponseHandler.handle(create200HttpResponse(explainResponseJson)).getOrElse(null)
    result mustNot be(null)
    result mustBe explainAnalyzeResponse
  }

}

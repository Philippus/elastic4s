package com.sksamuel.elastic4s.requests.index.analyze

import com.sksamuel.elastic4s.HttpEntity
import com.sksamuel.elastic4s.handlers.index.{AnalyseRequestContentBuilder, IndexHandlers}
import com.sksamuel.elastic4s.requests.indexes.analyze.AnalyzeRequest
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

class AnalyzeRequestHandlerTest extends AnyFunSuite with Matchers with IndexHandlers {

  test("analyzeRequestHandler work well") {
    val analyzeRequest = AnalyzeRequest(Array("你好世界"))
      .analyzer("smartcn")
      .index("testIndex")
    val result         = AnalyzeRequestHandler.build(analyzeRequest)

    result.method mustBe "GET"
    result.endpoint mustBe "/testIndex/_analyze"
    result.entity.foreach {
      case HttpEntity.ByteArrayEntity(content, contentCharset) =>
        new String(content, "UTF8") mustBe
          AnalyseRequestContentBuilder(analyzeRequest)
        contentCharset mustBe Some("application/json")
    }
  }

}

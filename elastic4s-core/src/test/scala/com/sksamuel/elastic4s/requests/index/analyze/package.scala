package com.sksamuel.elastic4s.requests.index

import com.sksamuel.elastic4s.HttpEntity.StringEntity
import com.sksamuel.elastic4s.HttpResponse
import com.sksamuel.elastic4s.requests.indexes.analyze.{ExplainAnalyzeDetail, ExplainAnalyzeResponse, ExplainAnalyzer, ExplainToken, NoExplainAnalyzeResponse, NoExplainToken, ExplainTokenFilters}

import scala.io.Source

package object analyze {

  def create200HttpResponse(body: String): HttpResponse = {
    HttpResponse(200, Some(StringEntity(body, None)), Map.empty)
  }

  def toHexBytes(str: String): String = str.map(_.toInt.toHexString).mkString("[", " ", "]")

  def readResource(name: String): String = {
    Source.fromInputStream(getClass.getResourceAsStream(name), "UTF-8")
      .getLines().mkString("\r\n")
  }

  def noExplainResponseJson: String = readResource("/analyze_request/helloworld_response.json")

  def noExplainResponse: NoExplainAnalyzeResponse = NoExplainAnalyzeResponse(Seq(
    NoExplainToken("hello", 0, 5, "<ALPHANUM>", 0),
    NoExplainToken("world", 6, 11, "<ALPHANUM>", 1)
  ))


  def explainResponseJson: String = readResource("/analyze_request/explain_helloworld_response.json")

  def explainAnalyzeResponse: ExplainAnalyzeResponse = ExplainAnalyzeResponse(
    ExplainAnalyzeDetail(
      customAnalyzer = false,
      Some(
        ExplainAnalyzer("standard", Seq(
          ExplainToken("hello", 0, 5, "<ALPHANUM>", 0, toHexBytes("hello"), 1, 1),
          ExplainToken("world", 6, 11, "<ALPHANUM>", 1, toHexBytes("world"), 1, 1)
        ))
      ))
  )

  def explainCustomTokenFilterResponseJson: String = readResource("/analyze_request/explain_custom_tokenfilter_response.json")

  def explainCustomTokenFilterResponse: ExplainAnalyzeResponse = ExplainAnalyzeResponse(
    ExplainAnalyzeDetail(
      customAnalyzer = true,
      None, Seq(
        ExplainTokenFilters(
          "snowball",
          Seq(
            ExplainToken("hello", 0, 5, "<ALPHANUM>", 0, toHexBytes("hello"), 1, 1, Some(false)),
            ExplainToken("world", 6, 11, "<ALPHANUM>", 1, toHexBytes("world"), 1, 1, Some(false))
          )
        )
      ))
  )


}

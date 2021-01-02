package com.sksamuel.elastic4s.requests.index

import com.sksamuel.elastic4s.HttpEntity.StringEntity
import com.sksamuel.elastic4s.HttpResponse
import com.sksamuel.elastic4s.requests.indexes.analyze.{ExplainAnalyzeDetail, ExplainAnalyzeResponse, ExplainAnalyzer, ExplainToken, NoExplainAnalyzeResponse, NoExplainToken}

package object analyze {

  def create200HttpResponse(body: String): HttpResponse = {
    HttpResponse(200, Some(StringEntity(body, None)), Map.empty)
  }

  def toHexBytes(str:String): String = str.map(_.toInt.toHexString).mkString("["," ","]")


  val noExplainResponseJson: String =
    """
      |{
      |  "tokens" : [
      |    {
      |      "token" : "hello",
      |      "start_offset" : 0,
      |      "end_offset" : 5,
      |      "type" : "<ALPHANUM>",
      |      "position" : 0
      |    },
      |    {
      |      "token" : "world",
      |      "start_offset" : 6,
      |      "end_offset" : 11,
      |      "type" : "<ALPHANUM>",
      |      "position" : 1
      |    }
      |  ]
      |}
      |""".stripMargin

  val noExplainJsonTokens : Seq[NoExplainToken] = Seq(
    NoExplainToken("hello", 0, 5, "<ALPHANUM>", 0),
    NoExplainToken("world", 6, 11, "<ALPHANUM>", 1)
  )
  val noExplainResponse: NoExplainAnalyzeResponse = NoExplainAnalyzeResponse(noExplainJsonTokens)

  val explainResponseJson:String =
    """
      |{
      |  "detail" : {
      |    "custom_analyzer" : false,
      |    "analyzer" : {
      |      "name" : "standard",
      |      "tokens" : [
      |        {
      |          "token" : "hello",
      |          "start_offset" : 0,
      |          "end_offset" : 5,
      |          "type" : "<ALPHANUM>",
      |          "position" : 0,
      |          "bytes" : "[68 65 6c 6c 6f]",
      |          "positionLength" : 1,
      |          "termFrequency" : 1
      |        },
      |        {
      |          "token" : "world",
      |          "start_offset" : 6,
      |          "end_offset" : 11,
      |          "type" : "<ALPHANUM>",
      |          "position" : 1,
      |          "bytes" : "[77 6f 72 6c 64]",
      |          "positionLength" : 1,
      |          "termFrequency" : 1
      |        }
      |      ]
      |    }
      |  }
      |}
      |""".stripMargin

  val explainTokens:Seq[ExplainToken] = Seq(
    ExplainToken("hello",0,5,"<ALPHANUM>",0,toHexBytes("hello"),1,1),
      ExplainToken("world",6,11,"<ALPHANUM>",1,toHexBytes("world"),1,1)
  )

  val explainAnalyzeResponse = ExplainAnalyzeResponse(
    ExplainAnalyzeDetail(customAnalyzer = false,
      ExplainAnalyzer("standard", explainTokens))
  )



}

package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.indexes.analyze.{ExplainAnalyzeResponse, ExplainToken, NoExplainAnalyzeResponse, NoExplainToken}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class AnalyzeRequestTests extends AnyFlatSpec with Matchers with DockerTests {

  def toHexBytes(str:String): String = str.map(_.toInt.toHexString).mkString("["," ","]")

  Try {
    client.execute {
      deleteIndex("testindex")
    }.await
  }


  "analyze request (explain = false)" should "return Token" in {
    val result = client.execute {
      analyze("hello world")
    }.await.result

    result match {
      case NoExplainAnalyzeResponse(tokens) =>
        tokens.length shouldBe 2
        tokens.head shouldBe NoExplainToken("hello",0,5,"<ALPHANUM>",0)
        tokens(1) shouldBe NoExplainToken("world",6,11,"<ALPHANUM>",1)
        println(result)
      case _ =>
        fail("should not be other Analyze response")
    }

  }

  "analyze request (explain = true)" should "return DetailToken" in {
    val result = client.execute {
      analyze("hello world")
        .explain(true)
    }.await.result

    result match {
      case ExplainAnalyzeResponse(detail) =>
        val tokens = detail.analyzer.tokens
        tokens.length shouldBe 2
        tokens.head shouldBe ExplainToken("hello",0,5,"<ALPHANUM>",0,toHexBytes("hello"),1,1)
        tokens(1) shouldBe   ExplainToken("world",6,11,"<ALPHANUM>",1,toHexBytes("world"),1,1)
        println(result)
      case _ =>
        fail("should not be other Analyze response")
    }
  }
}

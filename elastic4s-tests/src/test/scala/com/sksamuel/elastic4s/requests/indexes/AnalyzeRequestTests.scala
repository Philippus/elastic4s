package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.analysis.StopAnalyzer
import com.sksamuel.elastic4s.requests.analyzers.HtmlStripCharFilter
import com.sksamuel.elastic4s.requests.indexes.analyze._
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
        val tokens = detail.analyzer.get.tokens
        tokens.length shouldBe 2
        tokens.head shouldBe ExplainToken("hello",0,5,"<ALPHANUM>",0,toHexBytes("hello"),1,1)
        tokens(1) shouldBe   ExplainToken("world",6,11,"<ALPHANUM>",1,toHexBytes("world"),1,1)
        println(result)
      case _ =>
        fail("should not be other Analyze response")
    }
  }

  "analyze request custom tokenizer" should "work well" in {
    val result = client.execute {
      analyze("this is a <b>test</b>")
        .tokenizer("keyword")
    }.await.result

    result match {
      case NoExplainAnalyzeResponse(tokens) =>
        tokens.length shouldBe 1
        tokens.head shouldBe NoExplainToken("this is a <b>test</b>",0,21,"word",0)
        println(result)
      case _ =>
        fail("should not be other Analyze response")
    }
  }

  "analyze request explain custom filters" should "work well" in {
    val result = client.execute {
      AnalyzeRequest(Array("hello world"))
        .explain(true)
        .tokenizer("standard")
        .filters("snowball")
    }.await.result

    result shouldBe ExplainAnalyzeResponse(ExplainAnalyzeDetail(
      customAnalyzer = true,
      None,
      Seq(
        ExplainTokenFilters(
          "snowball",
          Seq(
            ExplainToken("hello", 0, 5, "<ALPHANUM>", 0, toHexBytes("hello"), 1, 1, Some(false)),
            ExplainToken("world", 6, 11, "<ALPHANUM>", 1, toHexBytes("world"), 1, 1, Some(false))
          )
        )
      )))
  }

  "analyze request custom filters raw type" should "work well" in {
    val result = client.execute {
      AnalyzeRequest(Array("hello world"))
        .tokenizer("keyword")
        .filters("lowercase","uppercase")
        .filters(StopAnalyzer("stop",List("a","is","this")))
    }.await.result

    result shouldBe NoExplainAnalyzeResponse(List(NoExplainToken("HELLO WORLD",0,11,"word",0)))
  }

  "analyze request using html_strip charFilters" should "work well" in {
    val result = client.execute {
      AnalyzeRequest(Array("<p>I&apos;m so <b>happy</b>!</p>"))
        .charFilters("html_strip")
    }.await.result

    result shouldBe NoExplainAnalyzeResponse(List(NoExplainToken("\nI'm so happy!\n",0,32,"word",0)))
  }
}

package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.analysis.StopAnalyzer
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


  "analyze request" should "work well" in {
    val result = client.execute {
      analyze("hello world")
    }.await.result

    result shouldBe NoExplainAnalyzeResponse(Seq(
      AnalyseToken("hello",0,5,"<ALPHANUM>",0),
      AnalyseToken("world",6,11,"<ALPHANUM>",1)
    ))
  }

  "standard analyze request text array and text" should "equal" in {
    val result = client.execute {
      analyze("hello world")
    }.await.result

    val resultSeq = client.execute {
      analyze("hello","world")
    }.await.result

    result shouldBe resultSeq
  }

  "analyze request with explain" should "work well" in {
    val result = client.execute {
      analyze("hello world")
        .explain(true)
    }.await.result

    result shouldBe ExplainAnalyzeResponse(ExplainAnalyzeDetail(
      customAnalyzer = false,
      Some(ExplainAnalyzer(
        "standard",
        Seq(
          AnalyseToken("hello",0,5,"<ALPHANUM>",0, Some(toHexBytes("hello")),Some(1),Some(1)),
          AnalyseToken("world",6,11,"<ALPHANUM>",1,Some(toHexBytes("world")),Some(1),Some(1))
        )
      ))
    ))
  }

  "analyze request custom tokenizer" should "work well" in {
    val result = client.execute {
      analyze("hello_world")
        .tokenizer("letter")
    }.await.result

    result shouldBe NoExplainAnalyzeResponse(
      List(
        AnalyseToken("hello", 0, 5, "word", 0),
        AnalyseToken("world", 6, 11, "word", 1))
    )

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
            AnalyseToken("hello", 0, 5, "<ALPHANUM>", 0, Some(toHexBytes("hello")), Some(1), Some(1), Some(false)),
            AnalyseToken("world", 6, 11, "<ALPHANUM>", 1, Some(toHexBytes("world")), Some(1), Some(1), Some(false))
          )
        )
      )))
  }

  "analyze request custom filters from analyzer" should "work well" in {
    val result = client.execute {
      AnalyzeRequest(Array("hello world"))
        .tokenizer("keyword")
        .filters("lowercase","uppercase")
        .filters(StopAnalyzer("stop",List("a","is","this")))
    }.await.result

    result shouldBe NoExplainAnalyzeResponse(List(AnalyseToken("HELLO WORLD",0,11,"word",0)))
  }

  "analyze request custom charFilters" should "work well" in {
    val result = client.execute {
      AnalyzeRequest(Array("<p>I&apos;m so <b>happy</b>!</p>"))
        .charFilters("html_strip")
    }.await.result

    result shouldBe NoExplainAnalyzeResponse(List(AnalyseToken("\nI'm so happy!\n",0,32,"word",0)))
  }

  "analyze request custom attributes" should "work well" in {
    val result = client.execute {
      analyze("hello","world")
        .explain(true)
        .tokenizer("standard")
        .filters("snowball")
        .attributes("keyword")
    }.await.result

    result shouldBe ExplainAnalyzeResponse(
      ExplainAnalyzeDetail(customAnalyzer = true, None,
        List(
          ExplainTokenFilters("snowball",
            List(
              AnalyseToken("hello", 0, 5,  "<ALPHANUM>", 0, None, None, None, Some(false)),
              AnalyseToken("world", 6, 11, "<ALPHANUM>", 1, None, None, None, Some(false))
            )
          ))
      )
    )
  }
}

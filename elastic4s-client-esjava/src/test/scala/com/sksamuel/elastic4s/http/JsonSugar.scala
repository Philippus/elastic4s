package com.sksamuel.elastic4s.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.scalatest.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}

trait JsonSugar extends Matchers {

  private val mapper = new ObjectMapper with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def matchJsonResource(resourceName: String) = new JsonResourceMatcher(resourceName)

  def matchJson(right: String) = new Matcher[String] {

    override def apply(left: String): MatchResult = {

      withClue(s"expected JSON [$right] ") {
        right should not be null
      }

      val expectedJson = mapper.readTree(right)
      val actualJson = mapper.readTree(left)

      MatchResult(
        expectedJson == actualJson,
        s"$actualJson did not match resource [$right]: $expectedJson",
        s"$actualJson did match resource [$right]: $expectedJson"
      )
    }
  }

  class JsonResourceMatcher(resourceName: String) extends Matcher[String] {
    override def apply(left: String): MatchResult = {
      val jsonResource = getClass.getResource(resourceName)
      withClue(s"expected JSON resource [$resourceName] ") {
        jsonResource should not be null
      }

      val expectedJson = mapper.readTree(jsonResource)
      val actualJson = mapper.readTree(left)

      MatchResult(
        expectedJson == actualJson,
        s"$actualJson did not match resource [$resourceName]: $expectedJson",
        s"$actualJson did match resource [$resourceName]: $expectedJson"
      )
    }
  }


}

package com.sksamuel.elastic4s

import com.commodityvectors.snapshotmatchers.SnapshotSerializer
import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.elasticsearch.common.xcontent.XContentBuilder
import org.scalatest.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}

trait JsonSugar extends Matchers {

  implicit lazy val xContentSerializer = new SnapshotSerializer[XContentBuilder] {
    val mapper = new ObjectMapper with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.enable(SerializationFeature.INDENT_OUTPUT)

    override def serialize(in: XContentBuilder): String = {
      val json = mapper.readTree(in.string())
      mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)
    }
  }

  protected val mapper = new ObjectMapper with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def matchJsonResource(resourceName: String) = new JsonResourceMatcher(resourceName)

  def matchJson(right: String) = new Matcher[String] {

    override def apply(left: String): MatchResult = {

      withClue(s"expected JSON [$right] ") {
        right should not be null
      }

      val expectedJson = mapper.readTree(left)
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

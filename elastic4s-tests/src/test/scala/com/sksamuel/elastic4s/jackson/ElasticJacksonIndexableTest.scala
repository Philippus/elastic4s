package com.sksamuel.elastic4s.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer, JsonMappingException, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Success

class ElasticJacksonIndexableTest extends WordSpec with Matchers with DockerTests {

  import ElasticJackson.Implicits._

  "ElasticJackson implicits" should {
    "index a case class" in {

      client.execute {
        bulk(
          indexInto("jacksontest" / "characters").source(Character("tyrion", "game of thrones")).withId("1"),
          indexInto("jacksontest" / "characters").source(Character("hank", "breaking bad")).withId("2"),
          indexInto("jacksontest" / "characters").source(Location("dorne", "game of thrones")).withId("3")
        ).refresh(RefreshPolicy.WaitFor)
      }.await
    }
    "read a case class" in {

      val resp = client.execute {
        search("jacksontest").query("breaking")
      }.await.result
      resp.to[Character] shouldBe List(Character("hank", "breaking bad"))

    }
    "populate special fields" in {

      val resp = client.execute {
        search("jacksontest").query("breaking")
      }.await.result

      // should populate _id, _index and _type for us from the search result
      resp.safeTo[CharacterWithIdTypeAndIndex] shouldBe
        List(Success(CharacterWithIdTypeAndIndex("2", "jacksontest", "characters", "hank", "breaking bad")))
    }
    "support custom mapper" in {

     implicit val custom: ObjectMapper with ScalaObjectMapper = new ObjectMapper with ScalaObjectMapper

      val module = new SimpleModule
      module.addDeserializer(classOf[String], new JsonDeserializer[String] {
        override def deserialize(p: JsonParser, ctxt: DeserializationContext): String = sys.error("boom")
      })
      custom.registerModule(module)

      val resp = client.execute {
        search("jacksontest").query("breaking")
      }.await.result

      // if our custom mapper has been picked up, then it should throw an exception when deserializing
      intercept[JsonMappingException] {
        resp.to[Character].toList
      }
    }
  }
}

case class Character(name: String, show: String)
case class CharacterWithIdTypeAndIndex(_id: String, _index: String, _type: String, name: String, show: String)
case class Location(name: String, show: String)

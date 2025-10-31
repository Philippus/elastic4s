package com.sksamuel.elastic4s.jackson

import tools.jackson.core.JsonParser
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.{DatabindException, DeserializationContext, ObjectMapper, ValueDeserializer}
import tools.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.util.Success

import tools.jackson.core.json.JsonFactory
import tools.jackson.databind.json.JsonMapper

class ElasticJacksonIndexableTest extends AnyWordSpec with Matchers with DockerTests {

  import ElasticJackson.Implicits._

  "ElasticJackson implicits" should {
    "index a case class" in {

      client.execute {
        bulk(
          indexInto("jacksontest").source(Character("tyrion", "game of thrones")).withId("1"),
          indexInto("jacksontest").source(Character("hank", "breaking bad")).withId("2"),
          indexInto("jacksontest").source(Location("dorne", "game of thrones")).withId("3")
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
      resp.safeTo[CharacterWithIdAndIndex] shouldBe
        List(Success(CharacterWithIdAndIndex("2", "jacksontest", "hank", "breaking bad")))
    }
    "support custom mapper" in {
      val module = new SimpleModule
      module.addDeserializer(
        classOf[String],
        (p: JsonParser, ctxt: DeserializationContext) => sys.error("boom")
      )

      implicit val custom: ObjectMapper with ClassTagExtensions =
        JsonMapper.builder(JsonFactory.builder().build())
          .addModule(DefaultScalaModule)
          .addModule(module)
          .build() :: ClassTagExtensions

      val resp = client.execute {
        search("jacksontest").query("breaking")
      }.await.result

      // if our custom mapper has been picked up, then it should throw an exception when deserializing
      intercept[DatabindException] {
        resp.to[Character].toList
      }
    }
  }
}

case class Character(name: String, show: String)
case class CharacterWithIdAndIndex(_id: String, _index: String, name: String, show: String)
case class Location(name: String, show: String)

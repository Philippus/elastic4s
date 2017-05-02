package com.sksamuel.elastic4s.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class ElasticJacksonIndexableTest extends WordSpec with Matchers with ElasticSugar with MockitoSugar {

  import ElasticJackson.Implicits._

  "ElasticJackson implicits" should {
    "index a case class" in {

      client.execute {
        bulk(
          indexInto("jacksontest" / "characters").source(Character("tyrion", "game of thrones")).withId(1),
          indexInto("jacksontest" / "characters").source(Character("hank", "breaking bad")).withId(2),
          indexInto("jacksontest" / "characters").source(Location("dorne", "game of thrones")).withId(3)
        )
      }

      blockUntilCount(3, "jacksontest")
    }
    "read a case class" in {

      val resp = client.execute {
        search("jacksontest" / "characters").query("breaking")
      }.await
      resp.to[Character] shouldBe List(Character("hank", "breaking bad"))

    }
    "populate special fields" in {

      val resp = client.execute {
        search("jacksontest" / "characters").query("breaking")
      }.await

      // should populate _id, _index and _type for us from the search result
      resp.safeTo[CharacterWithIdTypeAndIndex] shouldBe
        List(Right(CharacterWithIdTypeAndIndex("2", "jacksontest", "characters", "hank", "breaking bad")))
    }
    "support custom mapper" in {

      implicit val mapper: ObjectMapper = mock[ObjectMapper]
      Mockito.when(mapper.readTree(org.mockito.Matchers.anyString)).thenReturn(mock[ObjectNode])

      val resp = client.execute {
        search("jacksontest" / "characters").query("breaking")
      }.await
      // our custom mapper will just return null so that should be returned
      resp.to[Character].toList shouldBe List(null)
    }
  }
}

case class Character(name: String, show: String)
case class CharacterWithIdTypeAndIndex(_id: String, _index: String, _type: String, name: String, show: String)
case class Location(name: String, show: String)

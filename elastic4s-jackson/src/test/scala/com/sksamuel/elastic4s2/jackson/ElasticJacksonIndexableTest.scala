package com.sksamuel.elastic4s2.jackson

import com.sksamuel.elastic4s2.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class ElasticJacksonIndexableTest extends WordSpec with Matchers with ElasticSugar {

  import com.sksamuel.elastic4s2.ElasticDsl._
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
        searchIn("jacksontest" / "characters").query("breaking")
      }.await
      resp.to[Character] shouldBe List(Character("hank", "breaking bad"))

    }
    "populate special fields" in {

      val resp = client.execute {
        searchIn("jacksontest" / "characters").query("breaking")
      }.await

      // should populate _id, _index and _type for us from the search result
      resp.to[CharacterWithIdTypeAndIndex] shouldBe
        List(CharacterWithIdTypeAndIndex("2", "jacksontest", "characters", "hank", "breaking bad"))
    }
  }
}

case class Character(name: String, show: String)
case class CharacterWithIdTypeAndIndex(_id: String, _index: String, _type: String, name: String, show: String)
case class Location(name: String, show: String)

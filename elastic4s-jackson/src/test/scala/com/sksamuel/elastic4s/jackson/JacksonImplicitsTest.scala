package com.sksamuel.elastic4s.jackson

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class JacksonImplicitsTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._
  import ElasticJackson.Implicits._

  "jackson implicits" should {
    "index any type" in {

      client.execute {
        bulk(
          index into "jacksontest/characters" source Character("tyrion", "game of thrones") id 1,
          index into "jacksontest/characters" source Character("hank", "breaking bad") id 2,
          index into "jacksontest/characters" source Location("dorne", "game of thrones") id 3
        )
      }

      blockUntilCount(3, "jacksontest")
    }
    "read any type" in {

      blockUntilCount(3, "jacksontest")

      val resp = client.execute {
        search in "jacksontest/characters" query "breaking"
      }.await

      resp.hitsAs[Character].head shouldBe Character("hank", "breaking bad")
    }
    "read special fields with HitAs typeclass" in {

      blockUntilCount(3, "jacksontest")

      val resp = client.execute {
        search in "jacksontest/characters" query "breaking"
      }.await

      resp.as[CharacterWithIdTypeAndIndex].head shouldBe
        CharacterWithIdTypeAndIndex("2", "jacksontest", "characters", "hank", "breaking bad")
    }
  }

  implicit object CharacterIndexable extends Indexable[Character] {
    override def json(t: Character): String = s""" { "name" : "${t.name}", "show" : "${t.show}" } """
  }
}

case class Character(name: String, show: String)
case class CharacterWithIdTypeAndIndex(_id: String, _index: String, _type: String, name: String, show: String)
case class Location(name: String, show: String)
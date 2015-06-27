package com.sksamuel.elastic4s.jackson

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class JacksonImplicitsTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._
  import ElasticJackson.Implicits._

  "jackson implicits" should {
    "index any type" in {

      client.execute {
        bulk(
          index into "jacksontest/characters" source Character("tyrion", "game of thrones"),
          index into "jacksontest/characters" source Character("hank", "breaking bad"),
          index into "jacksontest/characters" source Location("dorne", "game of thrones")
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
  }
}

case class Character(name: String, show: String)
case class Location(name: String, show: String)
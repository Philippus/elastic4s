package com.sksamuel.elastic4s.jackson

import com.sksamuel.elastic4s.{ElasticSugar, ElasticDsl}
import org.scalatest.{WordSpec, Matchers}

class JacksonImplicitsTest extends WordSpec with Matchers with ElasticSugar {

  "jackson implicits" should {
    " support any type" in {

      import ElasticJackson.Implicits._
      import ElasticDsl._

      client.execute {
        bulk(
          index into "jacksontest/characters" source Character("tyrion", "game of thrones"),
          index into "jacksontest/characters" source Character("hank", "breaking bad"),
          index into "jacksontest/characters" source Location("dorne", "game of thrones")
        )
      }

      blockUntilCount(3, "jacksontest")
    }
  }
}

case class Character(name: String, show: String)
case class Location(name: String, show: String)
package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalactic.{Bad, ErrorMessage, Good, Or}
import org.scalatest.{Matchers, WordSpec}

class HitReaderTest extends WordSpec with Matchers with ElasticSugar with JsonSugar {

  import ElasticDsl._

  import scala.concurrent.ExecutionContext.Implicits.global

  client.execute {
    bulk(
      index into "cluedo/characters" fields("name" -> "professor plum", "career" -> "scientist"),
      index into "cluedo/characters" fields("name" -> "miss scarlet", "career" -> "media"),
      index into "cluedo/characters" fields("name" -> "rev green", "career" -> "minister")
    )
  }.await

  refresh("cluedo")
  blockUntilCount(3, "cluedo")

  val professorPlum = Killer("professor plum", "scientist")
  val scarlet = Killer("miss scarlet", "media")
  val revGreen = Killer("rev green", "minister")

  implicit object KillerHitReader extends HitReader[Killer] {
    override def from(hit: Hit): Killer Or ErrorMessage = {
      Good(
        Killer(
          hit.source("name").toString,
          hit.source("career").toString
        )
      )
    }
  }

  implicit object BadKillerHitReader extends HitReader[BadKiller] {
    override def from(hit: Hit): BadKiller Or ErrorMessage = {
      Bad("Missing field missingField")
    }
  }

  //  import HitFieldReader._
  //
  //  implicit object CharacterHitReader extends HitReader[Character] {
  //
  //    override def as(hit: RichSearchHit): Character Or Every[ErrorMessage] = {
  //      import Accumulation._
  //      val color = hit.richFields("color").validate[String]("Reading Character,")
  //      val sex = hit.richFields("sex").validate[String]("Reading Character,")
  //      val age = hit.richFields("age").validate[Int]("Reading Character,")
  //      withGood(color, sex, age)(Character)
  //    }
  //  }
  //
  //  implicit object KillerHitReader extends HitReader[Killer] {
  //
  //    override def as(hit: RichSearchHit): Killer Or Every[ErrorMessage] = {
  //      import Accumulation._
  //      val badName = hit.richFields("name").validate[String]("Reading Killer,")
  //      val weapons: Seq[String] Or Every[ErrorMessage] = hit.richFields("weapons").validate[Seq[String]]("Reading Killer,")
  //      val character: Option[Character] Or Every[ErrorMessage] = hit.richFields("character").validate[Option[Character]]("Reading Killer,")
  //      withGood(badName, weapons, character)(Killer)
  //    }
  //  }
  //
  //  implicit object BadKillerHitRead extends HitReader[BadKiller] {
  //
  //    override def as(hit: RichSearchHit): BadKiller Or Every[ErrorMessage] = {
  //      import Accumulation._
  //      val badName = hit.richFields("badName").validate[String]("Reading BadKiller,")
  //      withGood(badName)(BadKiller)
  //    }
  //  }

  "HitRead" should {
    "convert using an implicit HitRead to Array[Or[T, ErrorMessage]]" in {
      val killers = client.execute {
        search("cluedo" / "characters").query("*:*")
      }.map(_.to[Killer]).await
      killers.collect {
        case Good(x) => x
      }.toSet shouldBe Set(professorPlum, scarlet, revGreen)
    }
    "Include errors in the results" in {
      val killers = client.execute {
        search in "cluedo" / "characters" query "*:*"
      }.map(_.to[BadKiller]).await
      killers.collect {
        case Bad(x) => x
      }.toList shouldBe List("Missing field missingField", "Missing field missingField", "Missing field missingField")
    }
  }
}

case class Killer(name: String, career: String)
case class BadKiller(missingField: String)

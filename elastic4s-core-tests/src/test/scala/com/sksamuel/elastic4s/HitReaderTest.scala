package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalactic.{Bad, ErrorMessage, Accumulation, Every, Or}
import org.scalatest.{WordSpec, Matchers}

class HitReaderTest extends WordSpec with Matchers with ElasticSugar with JsonSugar {

  import ElasticDsl._

  client.execute {
    bulk(
      index into "cluedo/killers" fields("name" -> "professor plum", "weapons" -> Array("revolver", "knife")),
      index into "cluedo/killers" fields("name" -> "scarlet", "weapons" -> Array("candlestick",
        "spanner"), "character" -> Map("color" -> "red", "sex" -> "female", "age" -> 26)),
      index into "cluedo/killers" fields("name" -> "rev green", "weapons" -> Array("lead pipe",
        "rope"), "character" -> Map("color" -> "green", "sex" -> "male", "age" -> 45))
    )
  }.await

  refresh("cluedo")
  blockUntilCount(3, "cluedo")

  import HitFieldReader._

  implicit object CharacterHitReader extends HitReader[Character] {

    override def as(hit: RichSearchHit): Character Or Every[ErrorMessage] = {
      import Accumulation._
      val color = hit.richFields("color").validate[String]("Reading Character,")
      val sex = hit.richFields("sex").validate[String]("Reading Character,")
      val age = hit.richFields("age").validate[Int]("Reading Character,")
      withGood(color, sex, age)(Character)
    }
  }

  implicit object KillerHitReader extends HitReader[Killer] {

    override def as(hit: RichSearchHit): Killer Or Every[ErrorMessage] = {
      import Accumulation._
      val badName = hit.richFields("name").validate[String]("Reading Killer,")
      val weapons: Seq[String] Or Every[ErrorMessage] = hit.richFields("weapons").validate[Seq[String]]("Reading Killer,")
      val character: Option[Character] Or Every[ErrorMessage] = hit.richFields("character").validate[Option[Character]]("Reading Killer,")
      withGood(badName, weapons, character)(Killer)
    }
  }

  implicit object BadKillerHitRead extends HitReader[BadKiller] {

    override def as(hit: RichSearchHit): BadKiller Or Every[ErrorMessage] = {
      import Accumulation._
      val badName = hit.richFields("badName").validate[String]("Reading BadKiller,")
      withGood(badName)(BadKiller)
    }
  }

  val professorPlum = Killer("professor plum", Seq("revolver", "knife"))
  val scarlet = Killer("scarlet", Seq("candlestick", "spanner"), Some(Character("red", "female", 26)))
  val revGreen = Killer("rev green", Seq("lead pipe", "rope"), Some(Character("green", "male", 45)))

  "HitReader" should {
    "convert using an implicit HitReader to Array[Or[T, ErrorMessage]]" ignore {
      import Accumulation._
      val killers = client.execute {
        search in "cluedo" / "killers" query "*:*"
      }.map { resp => resp.readAs[Killer] }.await
      killers.combined.get.toSet shouldBe Set(professorPlum, scarlet, revGreen)
    }
    "accumulate conversion error using an implicit HitReader to Array[Or[T, ErrorMessage]]" ignore {
      import Accumulation._
      val killers = client.execute {
        search in "cluedo" / "killers" query "*:*"
      }.map { resp => resp.readAs[BadKiller] }.await
      killers.combined shouldBe Bad(Every(
        "BadKiller badName field is missing",
        "BadKiller badName field is missing",
        "BadKiller badName field is missing"
      ))
    }
  }
}

case class Character(color: String, sex: String, age: Int)
case class Killer(name: String, weapons: Seq[String], character: Option[Character] = None)
case class BadKiller(badName: String)

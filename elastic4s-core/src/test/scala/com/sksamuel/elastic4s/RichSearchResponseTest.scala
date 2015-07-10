package com.sksamuel.elastic4s

import org.scalactic._
import org.scalatest.{Matchers, WordSpec}

class RichSearchResponseTest extends WordSpec with Matchers with ElasticSugar with JsonSugar {

  import ElasticDsl._

  import scala.concurrent.ExecutionContext.Implicits.global

  client.execute {
    bulk(
      index into "cluedo/killers" fields ("name" -> "professor plum", "weapons"->Array("revolver","knife")),
      index into "cluedo/killers" fields ("name" -> "scarlet", "weapons"->Array("candlestick","spanner"),"character" -> Map("color"->"red", "sex"->"female","age"->26)),
      index into "cluedo/killers" fields ("name" -> "rev green","weapons"->Array("lead pipe", "rope"), "character" -> Map("color"->"green", "sex"->"male","age"->45))
    )
  }.await

  refresh("cluedo")
  blockUntilCount(3, "cluedo")

  implicit object KillerReader extends Reader[Killer] {
    override def read[T <: Killer : Manifest](json: String): T = mapper.readValue[T](json)
  }

  implicit object KillerHitAs extends HitAs[Killer] {
    import scala.collection.JavaConverters._
    override def as(hit: RichSearchHit): Killer = {
      val name= hit.sourceAsMap("name").toString
      val characterMapO = hit.sourceAsMap.get("character").map(_.asInstanceOf[java.util.Map[String,Any]].asScala.toMap)
      val characterO=characterMapO.map( characterMap=>
        Character(
          characterMap("color").toString,
          characterMap("sex").toString,
          characterMap("age").toString.toInt
        )
      )

      val weapons:Seq[String]=hit.sourceAsMap("weapons").asInstanceOf[java.util.ArrayList[String]].asScala.toList
      Killer(name, weapons,characterO)
    }
  }

  implicit object CharacterHitRead extends HitRead[Character] {
    import HitFieldRead._
    override def as(hit: RichSearchHitLike): Character Or Every[ErrorMessage] = {
      import Accumulation._
      val color= hit.richFields("color").validate[String]("Reading Character,")
      val sex= hit.richFields("sex").validate[String]("Reading Character,")
      val age= hit.richFields("age").validate[Int]("Reading Character,")
      withGood(color,sex,age)(Character)
    }
  }

  implicit object KillerHitRead extends HitRead[Killer] {
    import HitFieldRead._
    override def as(hit: RichSearchHitLike): Killer Or Every[ErrorMessage] = {
      import Accumulation._
      val badName= hit.richFields("name").validate[String]("Reading Killer,")
      val weapons: Seq[String] Or Every[ErrorMessage]= hit.richFields("weapons").validate[Seq[String]]("Reading Killer,")
      val character: Option[Character] Or Every[ErrorMessage]= hit.richFields("character").validate[Option[Character]]("Reading Killer,")
      withGood(badName,weapons,character)(Killer)
    }
  }
  implicit object BadKillerHitRead extends HitRead[BadKiller] {
    import HitFieldRead._
    override def as(hit: RichSearchHitLike): BadKiller Or Every[ErrorMessage] = {
      import Accumulation._
      val badName= hit.richFields("badName").validate[String]("Reading BadKiller,")
      withGood(badName)(BadKiller)
    }
  }

  val professorPlum = Killer("professor plum", Seq("revolver", "knife"))
  val scarlet = Killer("scarlet", Seq("candlestick", "spanner"), Some(Character("red", "female", 26)))
  val revGreen = Killer("rev green", Seq("lead pipe", "rope"), Some(Character("green", "male", 45)))

  "rich response" should {
    "convert using an implicit reader to Seq[T]" in {
      val killers = client.execute {
        search in "cluedo" / "killers" query "*:*"
      }.map { resp => resp.hitsAs[Killer] }.await
      killers.toSet shouldBe Set(professorPlum, scarlet ,revGreen )
    }
    "convert using an implicit HitAs to Seq[T]" in {
      val killers = client.execute {
        search in "cluedo" / "killers" query "*:*"
      }.map { resp => resp.as[Killer] }.await
      killers.toSet shouldBe Set(professorPlum, scarlet ,revGreen)
    }
    "convert using an implicit HitRead to Seq[T]" in {
      val killers = client.execute {
        search in "cluedo" / "killers" query "*:*"
      }.map { resp => resp.validate[Killer] }.await
      killers.get.toSet shouldBe Set(professorPlum, scarlet ,revGreen)
    }
    "accumulate conversion error using an implicit HitRead to Seq[T] " in {
      val killers = client.execute {
        search in "cluedo" / "killers" query "*:*"
      }.map { resp => resp.validate[BadKiller] }.await
      killers shouldBe Bad(Every("Reading BadKiller, badName field is missing","Reading BadKiller, badName field is missing","Reading BadKiller, badName field is missing"))
    }
  }
}

case class Character(color:String, sex:String, age:Int)
case class Killer(name: String,weapons:Seq[String], character:Option[Character]=None)
case class BadKiller(badName: String)

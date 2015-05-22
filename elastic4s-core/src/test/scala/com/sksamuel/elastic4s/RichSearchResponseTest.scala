package com.sksamuel.elastic4s

import org.scalatest.{ Matchers, WordSpec }

class RichSearchResponseTest extends WordSpec with Matchers with ElasticSugar with JsonSugar {

  import ElasticDsl._
  import scala.concurrent.ExecutionContext.Implicits.global

  client.execute {
    bulk(
      index into "cluedo/killers" fields ("name" -> "professor plum"),
      index into "cluedo/killers" fields ("name" -> "scarlet"),
      index into "cluedo/killers" fields ("name" -> "rev green")
    )
  }.await

  refresh("cluedo")
  blockUntilCount(3, "cluedo")

  "rich response" should {
    "convert using an implicit reader to Seq[T]" in {
      implicit val reader = new Reader[Killer] {
        override def read(source: String): Killer = mapper.readValue[Killer](source)
      }
      val killers = client.execute {
        search in "cluedo" / "killers" query "*:*"
      }.map { resp => resp.hitsAs[Killer] }.await
      killers.toSet shouldBe Set(Killer("professor plum"), Killer("scarlet"), Killer("rev green"))
    }
  }
}

case class Killer(name: String)

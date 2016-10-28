package com.sksamuel.elastic4s2.streams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s2.ElasticDsl2$
import com.sksamuel.elastic4s2.testkit.ElasticSugar
import org.scalatest.{WordSpec, Matchers}

class ScrollPublisherUnitTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl2._
  import ReactiveElastic._

  implicit val system = ActorSystem()

  "elastic-streams" should {
    "throw exception if search definition has no scroll" in {
      an [IllegalArgumentException] should be thrownBy
        client.publisher(search in "scrollpubint" / "emperors" query "*:*")
    }
  }
}

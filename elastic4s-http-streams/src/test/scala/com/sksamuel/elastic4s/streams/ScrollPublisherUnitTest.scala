package com.sksamuel.elastic4s.streams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, HttpElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class ScrollPublisherUnitTest extends WordSpec with Matchers with HttpElasticSugar with ClassloaderLocalNodeProvider {

  import ReactiveElastic._

  implicit val system = ActorSystem()

  "elastic-streams" should {
    "throw exception if search definition has no scroll" in {
      an [IllegalArgumentException] should be thrownBy
        http.publisher(search("scrollpubint" / "emperors") query "*:*")
    }
  }
}

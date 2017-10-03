package com.sksamuel.elastic4s.streams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, HttpElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class ScrollPublisherUnitTest extends WordSpec with Matchers with HttpElasticSugar with DiscoveryLocalNodeProvider {

  import ReactiveElastic._

  implicit val system = ActorSystem()

  "elastic-streams" should {
    "throw exception if search definition has no scroll" in {
      an [IllegalArgumentException] should be thrownBy
        http.publisher(search("scrollpubint") query "*:*")
    }
  }
}

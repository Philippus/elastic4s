package com.sksamuel.elastic4s.pekko.http.streams

import org.apache.pekko.actor.ActorSystem
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ScrollPublisherUnitTest extends AnyWordSpec with Matchers with DockerTests {

  import ReactiveElastic._

  implicit val system: ActorSystem = ActorSystem()

  "elastic-streams" should {
    "throw exception if search definition has no scroll" in {
      an [IllegalArgumentException] should be thrownBy
        client.publisher(search("scrollpubint") query "*:*")
    }
  }
}

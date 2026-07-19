package com.sksamuel.elastic4s.akka.reactivestreams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PaginatedPublisherUnitTest extends AnyWordSpec with Matchers with DockerTests {

  import ReactiveElastic._

  implicit val system: ActorSystem = ActorSystem()

  "elastic-streams" should {
    "throw exception if search definition has no scroll" in {
      an[IllegalArgumentException] should be thrownBy
        client.publisher(search("scrollpubint") query "*:*")
    }
    "throw exception if search definition has no sort for SearchAfter mode" in {
      an[IllegalArgumentException] should be thrownBy
        client.publisher(search("scrollpubint") query "*:*", SearchAfter)
    }
    "throw exception if search definition uses scroll with SearchAfter mode" in {
      an[IllegalArgumentException] should be thrownBy
        client.publisher(search("scrollpubint") query "*:*" scroll "1m" sortBy fieldSort("_doc"), SearchAfter)
    }
  }
}

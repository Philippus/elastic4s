package com.sksamuel.elastic4s.streams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.{ElasticSugar, SharedElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class ScrollPublisherUnitTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  import ReactiveElastic._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  implicit val system = ActorSystem()

  "elastic-streams" should {
    "throw exception if search definition has no scroll" in {
      an [IllegalArgumentException] should be thrownBy
        http.publisher(search("scrollpubint" / "emperors") query "*:*")
    }
  }
}

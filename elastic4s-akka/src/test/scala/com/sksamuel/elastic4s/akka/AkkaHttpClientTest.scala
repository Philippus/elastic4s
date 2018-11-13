package com.sksamuel.elastic4s.akka

import scala.util.Try

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.http.ElasticClient
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class AkkaHttpClientTest extends FlatSpec with Matchers with DockerTests with BeforeAndAfterAll {

  private implicit lazy val system = ActorSystem()

  override def beforeAll: Unit = {
    Try {
      client.execute {
        deleteIndex("testindex")
      }.await
    }
  }

  override def afterAll: Unit = {
    Try {
      client.execute {
        deleteIndex("testindex")
      }.await

      akkaClient.shutdown().await
      system.terminate().await
    }
  }

  private lazy val akkaClient = AkkaHttpClient(AkkaHttpClientSettings(List("localhost:9200")))

  override val client = ElasticClient(akkaClient)

  "AkkaHttpClient" should "support utf-8" in {
    client.execute {
      indexInto("testindex" / "testindex").doc("""{ "text":"¡Hola! ¿Qué tal?" }""")
    }.await.result.result shouldBe "created"
  }
}


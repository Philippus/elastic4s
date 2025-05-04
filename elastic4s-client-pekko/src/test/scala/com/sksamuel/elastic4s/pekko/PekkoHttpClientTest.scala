package com.sksamuel.elastic4s.pekko

import com.sksamuel.elastic4s.requests.common.HealthStatus
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.testkit.DockerTests.{elasticHost, elasticPort}
import com.sksamuel.elastic4s.{Authentication, CommonRequestOptions, ElasticClient}
import org.apache.pekko.actor.ActorSystem
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future
import scala.util.Try

class PekkoHttpClientTest extends AnyFlatSpec with Matchers with DockerTests with BeforeAndAfterAll {

  private implicit lazy val system: ActorSystem = ActorSystem()

  private lazy val pekkoClient = PekkoHttpClient(PekkoHttpClientSettings(List(s"$elasticHost:$elasticPort")))

  override val client: ElasticClient[Future] = ElasticClient(pekkoClient)

  override def beforeAll(): Unit = {
    Try {
      client.execute {
        deleteIndex("testindex")
      }.await
    }
  }

  override def afterAll(): Unit = {
    Try {
      client.execute {
        deleteIndex("testindex")
      }.await

      client.close().await
      system.terminate().await
    }
  }

  "PekkoHttpClient" should "support utf-8" in {

    client.execute {
      indexInto("testindex").doc("""{ "text":"¡Hola! ¿Qué tal?" }""")
    }.await.result.result shouldBe "created"
  }

  it should "work fine whith _cat endpoints " in {

    client.execute {
      catSegments()
    }.await.result

    client.execute {
      catShards()
    }.await.result

    client.execute {
      catNodes()
    }.await.result

    client.execute {
      catPlugins()
    }.await.result

    client.execute {
      catThreadPool()
    }.await.result

    client.execute {
      catHealth()
    }.await.result

    client.execute {
      catCount()
    }.await.result

    client.execute {
      catMaster()
    }.await.result

    client.execute {
      catAliases()
    }.await.result

    client.execute {
      catIndices()
    }.await.result

    client.execute {
      catIndices(HealthStatus.Green)
    }.await.result

    client.execute {
      catAllocation()
    }.await.result

  }

  it should "work with head methods" in {
    client.execute(
      indexExists("unknown_index")
    ).await.result
  }

  it should "propagate headers if included" in {
    implicit val requestOptions: CommonRequestOptions = CommonRequestOptions.defaults.copy(
      authentication = Authentication.UsernamePassword("user123", "pass123")
    )

    client.execute {
      catHealth()
    }.await.result.status shouldBe "401"
  }
}

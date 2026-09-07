package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.requests.indexes.CreateIndexResponse
import com.sksamuel.elastic4s.requests.indexes.admin.DeleteIndexResponse
import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl, ElasticProperties, Response}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try

object DockerTests {

  val elasticHost: String = sys.env.getOrElse("ES_HOST", "127.0.0.1")
  val elasticPort: String = sys.env.getOrElse("ES_PORT", "39227")

  // tracks whether the lazy val was ever forced, so the hook doesn't
  // instantiate a client purely in order to close it
  @volatile private var initialised = false

  private lazy val futureClient: ElasticClient[Future] = {
    val c = ElasticClient(JavaClient(ElasticProperties(s"http://$elasticHost:$elasticPort")))
    initialised = true
    c
  }

  Runtime.getRuntime.addShutdownHook(
    new Thread(() => closeClient(), "elastic4s-testkit-client-shutdown")
  )

  private def closeClient(): Unit =
    if (initialised) Try(Await.result(futureClient.close(), 10.seconds))
}

trait DockerTests extends ElasticDsl with FutureClientProvider {

  override def client: ElasticClient[Future] = DockerTests.futureClient

  protected def deleteIdx(indexName: String): Try[Response[DeleteIndexResponse]] = {
    Try {
      client.execute {
        ElasticDsl.deleteIndex(indexName)
      }.await
    }
  }

  protected def createIdx(name: String): Try[Response[CreateIndexResponse]] = Try {
    client.execute {
      createIndex(name)
    }.await
  }

  protected def createIdx(name: String, shards: Int): Try[Response[CreateIndexResponse]] = Try {
    client.execute {
      createIndex(name).shards(shards)
    }.await
  }

  protected def cleanIndex(indexName: String): Unit = {
    deleteIdx(indexName)
    createIdx(indexName)
  }
}

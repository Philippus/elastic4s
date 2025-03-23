package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.requests.indexes.CreateIndexResponse
import com.sksamuel.elastic4s.requests.indexes.admin.DeleteIndexResponse
import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl, ElasticProperties, Response}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object DockerTests {

  val elasticHost: String = sys.env.getOrElse("ES_HOST", "127.0.0.1")
  val elasticPort: String = sys.env.getOrElse(
    "ES_PORT",
    // use obscure ports for the tests to reduce the risk of interfering with existing elastic installations/containers
    "39227"
  )

  private lazy val futureClient: ElasticClient[Future] =
    ElasticClient(JavaClient(ElasticProperties(s"http://$elasticHost:$elasticPort")))

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

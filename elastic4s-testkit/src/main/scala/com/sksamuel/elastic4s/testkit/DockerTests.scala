package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl, ElasticProperties, Executor}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

trait DockerTests extends ElasticDsl with ClientProvider {

  val elasticHost = sys.env.getOrElse("ES_HOST", "127.0.0.1")
  val elasticPort = sys.env.getOrElse("ES_PORT",
      // use obscure ports for the tests to reduce the risk of interfering with existing elastic installations/containers
      "39227"
    )

  def mkJavaBasedClient(implicit executor: Executor[Future]): ElasticClient[Future] =
    ElasticClient(JavaClient(ElasticProperties(s"http://$elasticHost:$elasticPort")))

  // TODO: client is not closed, consider using beforeAll/afterAll to close it
  lazy val client: ElasticClient[Future] = mkJavaBasedClient

  protected def deleteIdx(indexName: String): Unit = {
    Try {
      client.execute {
        ElasticDsl.deleteIndex(indexName)
      }.await
    }
  }

  protected def createIdx(name: String) = Try {
    client.execute {
      createIndex(name)
    }.await
  }

  protected def createIdx(name: String, shards: Int) = Try {
    client.execute {
      createIndex(name).shards(shards)
    }.await
  }

  protected def cleanIndex(indexName: String): Unit = {
    deleteIdx(indexName)
    createIdx(indexName)
  }
}

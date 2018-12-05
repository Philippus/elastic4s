package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticClient, ElasticDsl, ElasticProperties}

import scala.util.Try

trait DockerTests extends com.sksamuel.elastic4s.http.ElasticDsl with ClientProvider {

  protected def elasticUri: String = "http://localhost:9200"

  val client = ElasticClient(ElasticProperties(elasticUri))

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

  protected def cleanIndex(indexName: String): Unit = {
    deleteIdx(indexName)
    Try {
      client.execute {
        ElasticDsl.createIndex(indexName)
      }.await
    }
  }
}

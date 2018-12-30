package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticDsl, ElasticProperties}

import scala.util.Try

trait DockerTests extends ElasticDsl with ClientProvider {

  val client = JavaClient(ElasticProperties("http://localhost:9200"))

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

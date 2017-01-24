package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.indexes.{CreateIndexContentBuilder, CreateIndexDefinition, DeleteIndexDefinition}
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._

case class DeleteIndexResponse()

trait IndexAdminExecutables {

  implicit object CreateIndexExecutable extends HttpExecutable[CreateIndexDefinition, CreateIndexResponse] {

    override def execute(client: RestClient, request: CreateIndexDefinition): (ResponseListener) => Any = {

      val method = "PUT"
      val endpoint = request.name

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.waitForActiveShards.foreach(params.put("wait_for_active_shards", _))

      val body = CreateIndexContentBuilder(request)
      client.performRequestAsync(method, endpoint, params.mapValues(_.toString).asJava, new StringEntity(body.string()), _)
    }
  }

  implicit object DeleteIndexExecutable extends HttpExecutable[DeleteIndexDefinition, DeleteIndexResponse] {
    override def execute(client: RestClient, request: DeleteIndexDefinition): (ResponseListener) => Any = {
      val method = "DELETE"
      val url = request.indexes.mkString(",")
      client.performRequestAsync(method, url, _)
    }
  }
}

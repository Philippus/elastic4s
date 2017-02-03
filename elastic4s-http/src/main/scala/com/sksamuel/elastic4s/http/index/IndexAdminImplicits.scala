package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.admin.{CloseIndexDefinition, IndexExistsDefinition, OpenIndexDefinition, RefreshIndexDefinition}
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.indexes.{CreateIndexContentBuilder, CreateIndexDefinition, DeleteIndexDefinition, IndexShowImplicits}
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._

case class DeleteIndexResponse()
case class RefreshIndexResponse()
case class IndexExistsResponse()
case class OpenIndexResponse(acknowledged: Boolean)
case class CloseIndexResponse(acknowledged: Boolean)

trait IndexAdminImplicits extends IndexShowImplicits {

  implicit object OpenIndexExecutable extends HttpExecutable[OpenIndexDefinition, OpenIndexResponse] {
    override def execute(client: RestClient, request: OpenIndexDefinition): (ResponseListener) => Any = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_open"
      client.performRequestAsync("POST", endpoint, _)
    }
  }

  implicit object CloseIndexExecutable extends HttpExecutable[CloseIndexDefinition, CloseIndexResponse] {
    override def execute(client: RestClient, request: CloseIndexDefinition): (ResponseListener) => Any = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_close"
      client.performRequestAsync("POST", endpoint, _)
    }
  }

  implicit object IndexExistsExecutable extends HttpExecutable[IndexExistsDefinition, Boolean] {
    override def execute(client: RestClient, request: IndexExistsDefinition): (ResponseListener) => Any = {
      val code = client.performRequest("HEAD", request.index).getStatusLine.getStatusCode
      resp => code == 200
    }
  }

  implicit object RefreshIndexExecutable extends HttpExecutable[RefreshIndexDefinition, RefreshIndexResponse] {
    override def execute(client: RestClient, request: RefreshIndexDefinition): (ResponseListener) => Any = {
      val url = "/" + request.indexes.mkString(",") + "/_refresh"
      val method = "POST"
      client.performRequestAsync(method, url, _)
    }
  }

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

package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.JsonFormat
import com.sksamuel.elastic4s.admin.{CloseIndexDefinition, IndexExistsDefinition, OpenIndexDefinition, RefreshIndexDefinition}
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.indexes.{CreateIndexContentBuilder, CreateIndexDefinition, DeleteIndexDefinition, IndexShowImplicits}
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.RestClient

import scala.collection.JavaConverters._
import scala.concurrent.Future

case class DeleteIndexResponse()
case class RefreshIndexResponse()
case class IndexExistsResponse()
case class OpenIndexResponse(acknowledged: Boolean)
case class CloseIndexResponse(acknowledged: Boolean)

trait IndexAdminImplicits extends IndexShowImplicits {

  implicit object OpenIndexExecutable extends HttpExecutable[OpenIndexDefinition, OpenIndexResponse] {
    override def execute(client: RestClient,
                         request: OpenIndexDefinition,
                         format: JsonFormat[OpenIndexResponse]): Future[OpenIndexResponse] = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_open"
      executeAsyncAndMapResponse(client.performRequestAsync("POST", endpoint, _), format)
    }
  }

  implicit object CloseIndexExecutable extends HttpExecutable[CloseIndexDefinition, CloseIndexResponse] {
    override def execute(client: RestClient,
                         request: CloseIndexDefinition,
                         format: JsonFormat[CloseIndexResponse]): Future[CloseIndexResponse] = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_close"
      executeAsyncAndMapResponse(client.performRequestAsync("POST", endpoint, _), format)
    }
  }

  implicit object IndexExistsExecutable extends HttpExecutable[IndexExistsDefinition, Boolean] {
    override def execute(client: RestClient,
                         request: IndexExistsDefinition,
                         format: JsonFormat[Boolean]): Future[Boolean] = {
      val code = client.performRequest("HEAD", request.index).getStatusLine.getStatusCode
      Future.successful(code == 200)
    }
  }

  implicit object RefreshIndexExecutable extends HttpExecutable[RefreshIndexDefinition, RefreshIndexResponse] {
    override def execute(client: RestClient,
                         request: RefreshIndexDefinition,
                         format: JsonFormat[RefreshIndexResponse]): Future[RefreshIndexResponse] = {
      val url = "/" + request.indexes.mkString(",") + "/_refresh"
      val method = "POST"
      executeAsyncAndMapResponse(client.performRequestAsync(method, url, _), format)
    }
  }

  implicit object CreateIndexExecutable extends HttpExecutable[CreateIndexDefinition, CreateIndexResponse] {

    override def execute(client: RestClient,
                         request: CreateIndexDefinition,
                         format: JsonFormat[CreateIndexResponse]): Future[CreateIndexResponse] = {

      val method = "PUT"
      val endpoint = "/" + request.name

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.waitForActiveShards.foreach(params.put("wait_for_active_shards", _))

      val body = CreateIndexContentBuilder(request)
      executeAsyncAndMapResponse(client.performRequestAsync(method, endpoint, params.mapValues(_.toString).asJava, new StringEntity(body.string()), _), format)
    }
  }

  implicit object DeleteIndexExecutable extends HttpExecutable[DeleteIndexDefinition, DeleteIndexResponse] {
    override def execute(client: RestClient,
                         request: DeleteIndexDefinition,
                         format: JsonFormat[DeleteIndexResponse]): Future[DeleteIndexResponse] = {
      val method = "DELETE"
      val url = "/" + request.indexes.mkString(",")
      executeAsyncAndMapResponse(client.performRequestAsync(method, url, _), format)
    }
  }
}

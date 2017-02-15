package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.JsonFormat
import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.http.{HttpExecutable, Shards}
import com.sksamuel.elastic4s.indexes.{CreateIndexContentBuilder, CreateIndexDefinition, DeleteIndexDefinition, IndexShowImplicits}
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._
import scala.concurrent.Future

case class DeleteIndexResponse(acknowledged: Boolean)
case class RefreshIndexResponse()
case class OpenIndexResponse(acknowledged: Boolean)
case class CloseIndexResponse(acknowledged: Boolean)

case class FlushIndexResponse(_shards: Shards) {
  def shards: Shards = _shards
}

case class TypeExistsResponse(exists: Boolean) {
  def isExists: Boolean = exists
}

case class IndexExistsResponse(exists: Boolean) {
  def isExists: Boolean = exists
}

case class ClearCacheResponse(_shards: Shards) {
  def shards: Shards = _shards
}

trait IndexAdminImplicits extends IndexShowImplicits {

  implicit object FlushIndexExecutable extends HttpExecutable[FlushIndexDefinition, FlushIndexResponse] {
    override def execute(client: RestClient,
                         request: FlushIndexDefinition,
                         format: JsonFormat[FlushIndexResponse]): Future[FlushIndexResponse] = {

      val endpoint = s"/${request.indexes.mkString(",")}/_flush"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.waitIfOngoing.map(_.toString).foreach(params.put("wait_if_ongoing", _))
      request.force.map(_.toString).foreach(params.put("force.map", _))

      val fn = client.performRequestAsync("POST", endpoint, params.asJava, _: ResponseListener)
      executeAsyncAndMapResponse(fn, format)
    }
  }

  implicit object ClearCacheExecutable extends HttpExecutable[ClearCacheDefinition, ClearCacheResponse] {
    override def execute(client: RestClient,
                         request: ClearCacheDefinition,
                         format: JsonFormat[ClearCacheResponse]): Future[ClearCacheResponse] = {

      val endpoint = s"/${request.indexes.mkString(",")}/_cache/clear"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.fieldDataCache.map(_.toString).foreach(params.put("fielddata", _))
      request.queryCache.map(_.toString).foreach(params.put("query", _))
      request.requestCache.map(_.toString).foreach(params.put("request", _))

      val fn = client.performRequestAsync("POST", endpoint, params.asJava, _: ResponseListener)
      executeAsyncAndMapResponse(fn, format)
    }
  }

  implicit object IndexExistsExecutable extends HttpExecutable[IndexExistsDefinition, IndexExistsResponse] {
    override def execute(client: RestClient,
                         request: IndexExistsDefinition,
                         format: JsonFormat[IndexExistsResponse]): Future[IndexExistsResponse] = {

      val endpoint = request.index
      logger.debug(s"Connecting to $endpoint for indexes exists check")
      val resp = client.performRequest("HEAD", endpoint)
      Future.successful(IndexExistsResponse(resp.getStatusLine.getStatusCode == 200))
    }
  }

  implicit object TypeExistsExecutable extends HttpExecutable[TypesExistsDefinition, TypeExistsResponse] {
    override def execute(client: RestClient,
                         request: TypesExistsDefinition,
                         format: JsonFormat[TypeExistsResponse]): Future[TypeExistsResponse] = {
      val endpoint = s"/${request.indexes.mkString(",")}/${request.types.mkString(",")}"
      logger.debug(s"Connecting to $endpoint for type exists check")
      val resp = client.performRequest("HEAD", endpoint)
      Future.successful(TypeExistsResponse(resp.getStatusLine.getStatusCode == 200))
    }
  }

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

      val body = CreateIndexContentBuilder(request).string()
      logger.debug(s"Executing create index $body")
      executeAsyncAndMapResponse(client.performRequestAsync(method, endpoint, params.mapValues(_.toString).asJava, new StringEntity(body), _), format)
    }
  }

  implicit object DeleteIndexExecutable extends HttpExecutable[DeleteIndexDefinition, DeleteIndexResponse] {
    override def execute(client: RestClient,
                         request: DeleteIndexDefinition,
                         format: JsonFormat[DeleteIndexResponse]): Future[DeleteIndexResponse] = {
      val method = "DELETE"
      val endpoint = "/" + request.indexes.mkString(",")
      logger.debug(s"Executing delete index $endpoint")
      executeAsyncAndMapResponse(client.performRequestAsync(method, endpoint, _), format)
    }
  }
}

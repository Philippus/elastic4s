package com.sksamuel.elastic4s.http.index.admin

import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import com.sksamuel.elastic4s.http.index.admin.IndexShardStoreResponse.StoreStatusResponse
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.indexes._
import com.sksamuel.elastic4s.indexes.admin.{ForceMergeDefinition, IndexRecoveryDefinition}
import com.sksamuel.elastic4s.mappings.PutMappingDefinition
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

trait IndexAdminImplicits extends IndexShowImplicits {

  implicit object IndexRecoveryHttpExecutable extends HttpExecutable[IndexRecoveryDefinition, IndexRecoveryResponse] {

    override def execute(client: RestClient, request: IndexRecoveryDefinition): Future[IndexRecoveryResponse] = {

      val endpoint = if (request.indices == Seq("_all") || request.indices.isEmpty) "/_recovery"
      else s"/${request.indices.mkString(",")}/_recovery"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.detailed.foreach(params.put("detailed", _))
      request.activeOnly.foreach(params.put("active_only", _))

      client.async("GET", endpoint, params.toMap, ResponseHandler.default)
    }
  }

  implicit object ForceMergeHttpExecutable extends HttpExecutable[ForceMergeDefinition, ForceMergeResponse] {

    override def execute(client: RestClient, request: ForceMergeDefinition): Future[ForceMergeResponse] = {

      val endpoint = if (request.indexes == Seq("_all") || request.indexes.isEmpty) "/_forcemerge"
      else s"/${request.indexes.mkString(",")}/_forcemerge"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.onlyExpungeDeletes.foreach(params.put("only_expunge_deletes", _))
      request.maxSegments.foreach(params.put("max_num_segments", _))
      request.flush.foreach(params.put("flush", _))

      client.async("POST", endpoint, params.toMap, ResponseHandler.default)
    }
  }

  implicit object FlushIndexHttpExecutable extends HttpExecutable[FlushIndexDefinition, FlushIndexResponse] {

    override def execute(client: RestClient, request: FlushIndexDefinition): Future[FlushIndexResponse] = {

      val endpoint = s"/${request.indexes.mkString(",")}/_flush"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.waitIfOngoing.map(_.toString).foreach(params.put("wait_if_ongoing", _))
      request.force.map(_.toString).foreach(params.put("force.map", _))

      client.async("POST", endpoint, params.toMap, ResponseHandler.default)
    }
  }

  implicit object ClearCacheHttpExecutable extends HttpExecutable[ClearCacheDefinition, ClearCacheResponse] {

    override def execute(client: RestClient, request: ClearCacheDefinition): Future[ClearCacheResponse] = {

      val endpoint = s"/${request.indexes.mkString(",")}/_cache/clear"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.fieldDataCache.map(_.toString).foreach(params.put("fielddata", _))
      request.queryCache.map(_.toString).foreach(params.put("query", _))
      request.requestCache.map(_.toString).foreach(params.put("request", _))

      client.async("POST", endpoint, params.toMap, ResponseHandler.default)
    }
  }

  implicit object IndexExistsHttpExecutable extends HttpExecutable[IndexExistsDefinition, IndexExistsResponse] {

    override def execute(client: RestClient,
                         request: IndexExistsDefinition): Future[IndexExistsResponse] = {

      val endpoint = request.index
      logger.debug(s"Connecting to $endpoint for indexes exists check")
      val resp = client.performRequest("HEAD", endpoint)
      Future.successful(IndexExistsResponse(resp.getStatusLine.getStatusCode == 200))
    }
  }

  implicit object TypeExistsHttpExecutable extends HttpExecutable[TypesExistsDefinition, TypeExistsResponse] {
    override def execute(client: RestClient,
                         request: TypesExistsDefinition): Future[TypeExistsResponse] = {
      val endpoint = s"/${request.indexes.mkString(",")}/${request.types.mkString(",")}"
      logger.debug(s"Connecting to $endpoint for type exists check")
      val resp = client.performRequest("HEAD", endpoint)
      Future.successful(TypeExistsResponse(resp.getStatusLine.getStatusCode == 200))
    }
  }

  implicit object AliasExistsHttpExecutable extends HttpExecutable[AliasExistsDefinition, AliasExistsResponse] {
    override def execute(client: RestClient,
                         request: AliasExistsDefinition): Future[AliasExistsResponse] = {
      val endpoint = s"/_alias/${request.alias}"
      logger.debug(s"Connecting to $endpoint for alias exists check")
      val resp = client.performRequest("HEAD", endpoint)
      Future.successful(AliasExistsResponse(resp.getStatusLine.getStatusCode == 200))
    }
  }

  implicit object OpenIndexHttpExecutable extends HttpExecutable[OpenIndexDefinition, OpenIndexResponse] {
    override def execute(client: RestClient,
                         request: OpenIndexDefinition): Future[OpenIndexResponse] = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_open"
      client.async("POST", endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object CloseIndexHttpExecutable extends HttpExecutable[CloseIndexDefinition, CloseIndexResponse] {
    override def execute(client: RestClient,
                         request: CloseIndexDefinition): Future[CloseIndexResponse] = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_close"
      client.async("POST", endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object RefreshIndexHttpExecutable extends HttpExecutable[RefreshIndexDefinition, RefreshIndexResponse] {
    override def execute(client: RestClient,
                         request: RefreshIndexDefinition): Future[RefreshIndexResponse] = {
      val endpoint = "/" + request.indexes.mkString(",") + "/_refresh"
      val method = "POST"
      client.async(method, endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object CreateIndexHttpExecutable extends HttpExecutable[CreateIndexDefinition, CreateIndexResponse] {

    override def execute(client: RestClient, request: CreateIndexDefinition): Future[CreateIndexResponse] = {

      val method = "PUT"
      val endpoint = "/" + request.name

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.waitForActiveShards.foreach(params.put("wait_for_active_shards", _))

      val body = CreateIndexContentBuilder(request).string()
      logger.debug(s"Executing create index $body")

      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.async(method, endpoint, Map.empty, entity, ResponseHandler.default)
    }
  }

  implicit object DeleteIndexHttpExecutable extends HttpExecutable[DeleteIndexDefinition, DeleteIndexResponse] {

    override def execute(client: RestClient, request: DeleteIndexDefinition): Future[DeleteIndexResponse] = {
      val method = "DELETE"
      val endpoint = "/" + request.indexes.mkString(",")
      logger.debug(s"Executing delete index $endpoint")

      client.async(method, endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object UpdateIndexLevelSettingsExecutable extends HttpExecutable[UpdateIndexLevelSettingsDefinition, UpdateIndexLevelSettingsResponse] {
    override def execute(client: RestClient, request: UpdateIndexLevelSettingsDefinition): Future[UpdateIndexLevelSettingsResponse] = {

      val method = "PUT"
      val endpoint = "/" + request.indexes.mkString(",") + "/_settings"
      val body = UpdateIndexLevelSettingsBuilder(request).string()
      logger.debug(s"Executing update index level settings $body")

      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.async(method, endpoint, Map.empty, entity, ResponseHandler.default)
    }
  }

  implicit object IndexShardStoreExecutable extends HttpExecutable[IndexShardStoreDefinition, StoreStatusResponse] {

    override def execute(client: RestClient, request: IndexShardStoreDefinition): Future[StoreStatusResponse] = {
      val method = "GET"
      val endpoint = "/" + request.indexes.values.mkString(",") + "/_shard_stores"
      val params = scala.collection.mutable.Map.empty[String, String]
      request.status.foreach(params.put("status", _))
      logger.debug(s"Accesing endpoint $endpoint")

      client.async(method, endpoint, params.toMap, ResponseHandler.default)
    }
  }
}

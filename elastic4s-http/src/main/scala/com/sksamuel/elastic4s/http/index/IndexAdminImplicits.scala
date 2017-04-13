package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.http.index.IndexShardStoreResponse.StoreStatusResponse
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler, Shards}
import com.sksamuel.elastic4s.indexes._
import com.sksamuel.elastic4s.mappings.PutMappingDefinition
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

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

case class AliasExistsResponse(exists: Boolean) {
  def isExists: Boolean = exists
}

case class ClearCacheResponse(_shards: Shards) {
  def shards: Shards = _shards
}

case class UpdateIndexLevelSettingsResponse(acknowledged: Boolean) {
  def success: Boolean = acknowledged
}

case class IndicesAliasResponse(acknowledged: Boolean) {
  def success: Boolean = acknowledged
}

case class PutMappingResponse(acknowledged: Boolean) {
  def success: Boolean = acknowledged
}

object IndexShardStoreResponse {
  case class StoreStatusResponse(indices: Map[String, IndexStoreStatus])
  case class IndexStoreStatus(shards: Map[String, ShardStoreStatus])
  type StoreStatus = Map[String, AnyRef]

  case class ShardStoreStatus(stores: Seq[StoreStatus])
}



trait IndexAdminImplicits extends IndexShowImplicits {

  implicit object FlushIndexExecutable extends HttpExecutable[FlushIndexDefinition, FlushIndexResponse] {

    override def execute(client: RestClient, request: FlushIndexDefinition): Future[FlushIndexResponse] = {

      val endpoint = s"/${request.indexes.mkString(",")}/_flush"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.waitIfOngoing.map(_.toString).foreach(params.put("wait_if_ongoing", _))
      request.force.map(_.toString).foreach(params.put("force.map", _))

      client.future("POST", endpoint, params.toMap, ResponseHandler.default)
    }
  }

  implicit object ClearCacheExecutable extends HttpExecutable[ClearCacheDefinition, ClearCacheResponse] {

    override def execute(client: RestClient, request: ClearCacheDefinition): Future[ClearCacheResponse] = {

      val endpoint = s"/${request.indexes.mkString(",")}/_cache/clear"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.fieldDataCache.map(_.toString).foreach(params.put("fielddata", _))
      request.queryCache.map(_.toString).foreach(params.put("query", _))
      request.requestCache.map(_.toString).foreach(params.put("request", _))

      client.future("POST", endpoint, params.toMap, ResponseHandler.default)
    }
  }

  implicit object IndexExistsExecutable extends HttpExecutable[IndexExistsDefinition, IndexExistsResponse] {

    override def execute(client: RestClient,
                         request: IndexExistsDefinition): Future[IndexExistsResponse] = {

      val endpoint = request.index
      logger.debug(s"Connecting to $endpoint for indexes exists check")
      val resp = client.performRequest("HEAD", endpoint)
      Future.successful(IndexExistsResponse(resp.getStatusLine.getStatusCode == 200))
    }
  }

  implicit object TypeExistsExecutable extends HttpExecutable[TypesExistsDefinition, TypeExistsResponse] {
    override def execute(client: RestClient,
                         request: TypesExistsDefinition): Future[TypeExistsResponse] = {
      val endpoint = s"/${request.indexes.mkString(",")}/${request.types.mkString(",")}"
      logger.debug(s"Connecting to $endpoint for type exists check")
      val resp = client.performRequest("HEAD", endpoint)
      Future.successful(TypeExistsResponse(resp.getStatusLine.getStatusCode == 200))
    }
  }

  implicit object AliasExistsExecutable extends HttpExecutable[AliasExistsDefinition, AliasExistsResponse] {
    override def execute(client: RestClient,
                         request: AliasExistsDefinition): Future[AliasExistsResponse] = {
      val endpoint = s"/_alias/${request.alias}"
      logger.debug(s"Connecting to $endpoint for alias exists check")
      val resp = client.performRequest("HEAD", endpoint)
      Future.successful(AliasExistsResponse(resp.getStatusLine.getStatusCode == 200))
    }
  }

  implicit object OpenIndexExecutable extends HttpExecutable[OpenIndexDefinition, OpenIndexResponse] {
    override def execute(client: RestClient,
                         request: OpenIndexDefinition): Future[OpenIndexResponse] = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_open"
      client.future("POST", endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object CloseIndexExecutable extends HttpExecutable[CloseIndexDefinition, CloseIndexResponse] {
    override def execute(client: RestClient,
                         request: CloseIndexDefinition): Future[CloseIndexResponse] = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_close"
      client.future("POST", endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object RefreshIndexExecutable extends HttpExecutable[RefreshIndexDefinition, RefreshIndexResponse] {
    override def execute(client: RestClient,
                         request: RefreshIndexDefinition): Future[RefreshIndexResponse] = {
      val endpoint = "/" + request.indexes.mkString(",") + "/_refresh"
      val method = "POST"
      client.future(method, endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object CreateIndexExecutable extends HttpExecutable[CreateIndexDefinition, CreateIndexResponse] {


    override def execute(client: RestClient, request: CreateIndexDefinition): Future[CreateIndexResponse] = {

      val method = "PUT"
      val endpoint = "/" + request.name

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.waitForActiveShards.foreach(params.put("wait_for_active_shards", _))

      val body = CreateIndexContentBuilder(request).string()
      logger.debug(s"Executing create index $body")

      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.future(method, endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object DeleteIndexExecutable extends HttpExecutable[DeleteIndexDefinition, DeleteIndexResponse] {

    override def execute(client: RestClient, request: DeleteIndexDefinition): Future[DeleteIndexResponse] = {
      val method = "DELETE"
      val endpoint = "/" + request.indexes.mkString(",")
      logger.debug(s"Executing delete index $endpoint")

      client.future(method, endpoint, Map.empty, ResponseHandler.default)
    }
  }

  implicit object UpdateIndexLevelSettingsExecutable extends HttpExecutable[UpdateIndexLevelSettingsDefinition, UpdateIndexLevelSettingsResponse] {
    override def execute(client: RestClient, request: UpdateIndexLevelSettingsDefinition): Future[UpdateIndexLevelSettingsResponse] = {

      val method = "PUT"
      val endpoint = "/" + request.indexes.mkString(",") + "/_settings"
      val body = UpdateIndexLevelSettingsBuilder(request).string()
      logger.debug(s"Executing update index level settings $body")

      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.future(method, endpoint, Map.empty, entity, ResponseHandler.default)
    }
  }

  implicit object IndexShardStoreExecutable extends HttpExecutable[IndexShardStoreDefinition, StoreStatusResponse] {

    override def execute(client: RestClient, request: IndexShardStoreDefinition): Future[StoreStatusResponse] = {
      val method = "GET"
      val endpoint = "/" + request.indexes.values.mkString(",") + "/_shard_stores"
      val params = scala.collection.mutable.Map.empty[String, String]
      request.status.foreach(params.put("status", _))
      logger.debug(s"Accesing endpoint $endpoint")

      client.future(method, endpoint, params.toMap, ResponseHandler.default)
    }
  }

  implicit object PutMappingExecutable extends HttpExecutable[PutMappingDefinition, PutMappingResponse] {

    override def execute(client: RestClient, request: PutMappingDefinition): Future[PutMappingResponse] = {
      val method = "PUT"
      val endpoint = s"/${request.indexesAndType.indexes.mkString(",")}/_mapping/${request.indexesAndType.`type`}"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.updateAllTypes.foreach(params.put("update_all_types", _))
      request.ignoreUnavailable.foreach(params.put("ignore_unavailable", _))
      request.allowNoIndices.foreach(params.put("allow_no_indices", _))
      request.expandWildcards.foreach(params.put("expand_wildcards", _))


      val body = PutMappingBuilder(request).string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)
      logger.debug(s"Executing Put Mapping request to '$endpoint $body'")

      client.future(method, endpoint, params.toMap, entity, ResponseHandler.default)
    }
  }
}

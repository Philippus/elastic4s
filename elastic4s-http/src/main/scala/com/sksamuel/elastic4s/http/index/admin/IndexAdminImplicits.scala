package com.sksamuel.elastic4s.http.index.admin

import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import com.sksamuel.elastic4s.http.index.admin.IndexShardStoreResponse.StoreStatusResponse
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.indexes._
import com.sksamuel.elastic4s.indexes.admin.{ForceMergeDefinition, IndexRecoveryDefinition}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{Response, RestClient}

import scala.concurrent.Future
import scala.util.{Success, Try}

trait IndexAdminImplicits extends IndexShowImplicits {

  implicit object IndexRecoveryHttpExecutable extends HttpExecutable[IndexRecoveryDefinition, IndexRecoveryResponse] {

    override def execute(client: RestClient, request: IndexRecoveryDefinition): Future[Response] = {

      val endpoint = if (request.indices == Seq("_all") || request.indices.isEmpty) "/_recovery"
      else s"/${request.indices.mkString(",")}/_recovery"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.detailed.foreach(params.put("detailed", _))
      request.activeOnly.foreach(params.put("active_only", _))

      client.async("GET", endpoint, params.toMap)
    }
  }

  implicit object ForceMergeHttpExecutable extends HttpExecutable[ForceMergeDefinition, ForceMergeResponse] {

    override def execute(client: RestClient, request: ForceMergeDefinition): Future[Response] = {

      val endpoint = if (request.indexes == Seq("_all") || request.indexes.isEmpty) "/_forcemerge"
      else s"/${request.indexes.mkString(",")}/_forcemerge"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.onlyExpungeDeletes.foreach(params.put("only_expunge_deletes", _))
      request.maxSegments.foreach(params.put("max_num_segments", _))
      request.flush.foreach(params.put("flush", _))

      client.async("POST", endpoint, params.toMap)
    }
  }

  implicit object FlushIndexHttpExecutable extends HttpExecutable[FlushIndexDefinition, FlushIndexResponse] {

    override def execute(client: RestClient, request: FlushIndexDefinition): Future[Response] = {

      val endpoint = s"/${request.indexes.mkString(",")}/_flush"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.waitIfOngoing.map(_.toString).foreach(params.put("wait_if_ongoing", _))
      request.force.map(_.toString).foreach(params.put("force.map", _))

      client.async("POST", endpoint, params.toMap)
    }
  }

  implicit object ClearCacheHttpExecutable extends HttpExecutable[ClearCacheDefinition, ClearCacheResponse] {

    override def execute(client: RestClient, request: ClearCacheDefinition): Future[Response] = {

      val endpoint = s"/${request.indexes.mkString(",")}/_cache/clear"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.fieldDataCache.map(_.toString).foreach(params.put("fielddata", _))
      request.queryCache.map(_.toString).foreach(params.put("query", _))
      request.requestCache.map(_.toString).foreach(params.put("request", _))

      client.async("POST", endpoint, params.toMap)
    }
  }

  implicit object IndexExistsHttpExecutable extends HttpExecutable[IndexExistsDefinition, IndexExistsResponse] {

    override def responseHandler: ResponseHandler[IndexExistsResponse] = new ResponseHandler[IndexExistsResponse] {
      override def onResponse(response: Response): Try[IndexExistsResponse] = {
        Success(IndexExistsResponse(response.getStatusLine.getStatusCode == 200))
      }
    }

    override def execute(client: RestClient, request: IndexExistsDefinition): Future[Response] = {
      val endpoint = request.index
      client.async("HEAD", endpoint, Map.empty)
    }
  }

  implicit object TypeExistsHttpExecutable extends HttpExecutable[TypesExistsDefinition, TypeExistsResponse] {

    override def execute(client: RestClient, request: TypesExistsDefinition): Future[Response] = {
      val endpoint = s"/${request.indexes.mkString(",")}/${request.types.mkString(",")}"
      client.async("HEAD", endpoint, Map.empty)
    }

    override def responseHandler: ResponseHandler[TypeExistsResponse] = new ResponseHandler[TypeExistsResponse] {
      override def onResponse(resp: Response): Try[TypeExistsResponse] = Success(TypeExistsResponse(resp.getStatusLine.getStatusCode == 200))
    }
  }

  implicit object AliasExistsHttpExecutable extends HttpExecutable[AliasExistsDefinition, AliasExistsResponse] {

    override def execute(client: RestClient, request: AliasExistsDefinition): Future[Response] = {
      val endpoint = s"/_alias/${request.alias}"
      client.async("HEAD", endpoint, Map.empty)
    }

    override def responseHandler: ResponseHandler[AliasExistsResponse] = new ResponseHandler[AliasExistsResponse] {
      override def onResponse(resp: Response): Try[AliasExistsResponse] = Success(AliasExistsResponse(resp.getStatusLine.getStatusCode == 200))
    }
  }

  implicit object OpenIndexHttpExecutable extends HttpExecutable[OpenIndexDefinition, OpenIndexResponse] {
    override def execute(client: RestClient, request: OpenIndexDefinition): Future[Response] = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_open"
      client.async("POST", endpoint, Map.empty)
    }
  }

  implicit object CloseIndexHttpExecutable extends HttpExecutable[CloseIndexDefinition, CloseIndexResponse] {
    override def execute(client: RestClient, request: CloseIndexDefinition): Future[Response] = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_close"
      client.async("POST", endpoint, Map.empty)
    }
  }

  implicit object RefreshIndexHttpExecutable extends HttpExecutable[RefreshIndexDefinition, RefreshIndexResponse] {
    override def execute(client: RestClient, request: RefreshIndexDefinition): Future[Response] = {
      val endpoint = "/" + request.indexes.mkString(",") + "/_refresh"
      client.async("POST", endpoint, Map.empty)
    }
  }

  implicit object CreateIndexHttpExecutable extends HttpExecutable[CreateIndexDefinition, CreateIndexResponse] {

    override def execute(client: RestClient, request: CreateIndexDefinition): Future[Response] = {

      val endpoint = "/" + request.name

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.waitForActiveShards.foreach(params.put("wait_for_active_shards", _))

      val body = CreateIndexContentBuilder(request).string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.async("PUT", endpoint, Map.empty, entity)
    }
  }

  implicit object DeleteIndexHttpExecutable extends HttpExecutable[DeleteIndexDefinition, DeleteIndexResponse] {

    override def execute(client: RestClient, request: DeleteIndexDefinition): Future[Response] = {
      val endpoint = "/" + request.indexes.mkString(",")
      client.async("DELETE", endpoint, Map.empty)
    }
  }

  implicit object UpdateIndexLevelSettingsExecutable extends HttpExecutable[UpdateIndexLevelSettingsDefinition, UpdateIndexLevelSettingsResponse] {
    override def execute(client: RestClient, request: UpdateIndexLevelSettingsDefinition): Future[Response] = {

      val endpoint = "/" + request.indexes.mkString(",") + "/_settings"
      val body = UpdateIndexLevelSettingsBuilder(request).string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.async("PUT", endpoint, Map.empty, entity)
    }
  }

  implicit object IndexShardStoreExecutable extends HttpExecutable[IndexShardStoreDefinition, StoreStatusResponse] {

    override def execute(client: RestClient, request: IndexShardStoreDefinition): Future[Response] = {

      val endpoint = "/" + request.indexes.values.mkString(",") + "/_shard_stores"
      val params = scala.collection.mutable.Map.empty[String, String]
      request.status.foreach(params.put("status", _))

      client.async("GET", endpoint, params.toMap)
    }
  }
}

package com.sksamuel.elastic4s.requests.indexes.admin

import com.sksamuel.elastic4s.requests.admin.{AliasExistsRequest, ClearCacheRequest, CloseIndexRequest, FlushIndexRequest, GetSegmentsRequest, IndexShardStoreRequest, IndicesExistsRequest, OpenIndexRequest, RefreshIndexRequest, ShrinkIndexRequest, TypesExistsRequest, UpdateIndexLevelSettingsRequest}
import com.sksamuel.elastic4s.requests.common.IndicesOptionsParams
import com.sksamuel.elastic4s.requests.indexes._
import com.sksamuel.elastic4s.requests.indexes.admin.IndexShardStoreResponse.StoreStatusResponse
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, ResponseHandler, XContentFactory, ElasticUrlEncoder}

case class ShrinkIndexResponse()

trait IndexAdminHandlers {

  implicit object ShrinkIndexHandler extends Handler[ShrinkIndexRequest, ShrinkIndexResponse] {

    override def build(request: ShrinkIndexRequest): ElasticRequest = {

      val endpoint = s"/${request.source}/_shrink/${request.target}"

      val params = scala.collection.mutable.Map.empty[String, Any]

      val builder = XContentFactory.jsonBuilder()
      if (request.settings.nonEmpty) {
        builder.startObject("settings")
        for ((key, value) <- request.settings)
          builder.field(key, value)
        builder.endObject()
      }

      val entity = HttpEntity(builder.string, "application/json")
      ElasticRequest("POST", endpoint, params.toMap, entity)
    }
  }

  implicit object IndexRecoveryHandler extends Handler[IndexRecoveryRequest, IndexRecoveryResponse] {

    override def build(request: IndexRecoveryRequest): ElasticRequest = {

      val endpoint =
        if (request.indices == Seq("_all") || request.indices.isEmpty) "/_recovery"
        else s"/${request.indices.mkString(",")}/_recovery"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.detailed.foreach(params.put("detailed", _))
      request.activeOnly.foreach(params.put("active_only", _))

      ElasticRequest("GET", endpoint, params.toMap)
    }
  }

  implicit object ForceMergeHandler extends Handler[ForceMergeRequest, ForceMergeResponse] {

    override def build(request: ForceMergeRequest): ElasticRequest = {

      val endpoint =
        if (request.indexes == Seq("_all") || request.indexes.isEmpty) "/_forcemerge"
        else s"/${request.indexes.mkString(",")}/_forcemerge"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.onlyExpungeDeletes.foreach(params.put("only_expunge_deletes", _))
      request.maxSegments.foreach(params.put("max_num_segments", _))
      request.flush.foreach(params.put("flush", _))

      ElasticRequest("POST", endpoint, params.toMap)
    }
  }

  implicit object FlushIndexHandler extends Handler[FlushIndexRequest, FlushIndexResponse] {

    override def build(request: FlushIndexRequest): ElasticRequest = {

      val endpoint = s"/${request.indexes.mkString(",")}/_flush"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.waitIfOngoing.map(_.toString).foreach(params.put("wait_if_ongoing", _))
      request.force.map(_.toString).foreach(params.put("force.map", _))

      ElasticRequest("POST", endpoint, params.toMap)
    }
  }

  implicit object ClearCacheHandler extends Handler[ClearCacheRequest, ClearCacheResponse] {

    override def build(request: ClearCacheRequest): ElasticRequest = {

      val endpoint = s"/${request.indexes.mkString(",")}/_cache/clear"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.fieldDataCache.map(_.toString).foreach(params.put("fielddata", _))
      request.queryCache.map(_.toString).foreach(params.put("query", _))
      request.requestCache.map(_.toString).foreach(params.put("request", _))

      ElasticRequest("POST", endpoint, params.toMap)
    }
  }

  implicit object IndexExistsHandler extends Handler[IndicesExistsRequest, IndexExistsResponse] {

    override def responseHandler: ResponseHandler[IndexExistsResponse] = new ResponseHandler[IndexExistsResponse] {
      override def handle(resp: HttpResponse): Either[ElasticError, IndexExistsResponse] = resp.statusCode match {
        case 200 => Right(IndexExistsResponse(true))
        case 404 => Right(IndexExistsResponse(false))
        case code => Left(ElasticError.fromThrowable(new RuntimeException(s"Error with index exists request (http code $code")))
      }
    }

    override def build(request: IndicesExistsRequest): ElasticRequest = {
      val endpoint = s"/${request.indexes.string(true)}"

      val params = request.indicesOptions.map(IndicesOptionsParams(_)).getOrElse(Map.empty)

      ElasticRequest("HEAD", endpoint, params)
    }
  }

  implicit object GetSegmentHandler extends Handler[GetSegmentsRequest, GetSegmentsResponse] {
    override def build(request: GetSegmentsRequest): ElasticRequest = {
      val endpoint = if (request.indexes.isAll) "/_segments" else s"/${request.indexes.string(true)}/_segments"
      ElasticRequest("GET", endpoint, Map("verbose" -> "true"))
    }
  }

  implicit object TypeExistsHandler extends Handler[TypesExistsRequest, TypeExistsResponse] {

    override def responseHandler: ResponseHandler[TypeExistsResponse] = new ResponseHandler[TypeExistsResponse] {
      override def handle(response: HttpResponse) = Right(TypeExistsResponse(response.statusCode == 200))
    }

    override def build(request: TypesExistsRequest): ElasticRequest = {
      val endpoint = s"/${request.indexes.mkString(",")}/_mapping/${request.types.mkString(",")}"
      ElasticRequest("HEAD", endpoint)
    }
  }

  implicit object AliasExistsHandler extends Handler[AliasExistsRequest, AliasExistsResponse] {

    override def build(request: AliasExistsRequest): ElasticRequest = {
      val endpoint = s"/_alias/${request.alias}"
      ElasticRequest("HEAD", endpoint)
    }

    override def responseHandler: ResponseHandler[AliasExistsResponse] = new ResponseHandler[AliasExistsResponse] {
      override def handle(resp: HttpResponse) =
        Right(AliasExistsResponse(resp.statusCode == 200))
    }
  }

  implicit object OpenIndexHandler extends Handler[OpenIndexRequest, OpenIndexResponse] {

    private val Method = "POST"

    override def build(request: OpenIndexRequest): ElasticRequest = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_open?"
      val endpoint2 = request.ignoreUnavailable.fold(endpoint)(ignore => s"${endpoint}ignore_unavailable=$ignore")
      val endpoint3 = request.waitForActiveShards.fold(endpoint2)(count => s"$endpoint&wait_for_active_shards=$count")
      ElasticRequest(Method, endpoint3)
    }
  }

  implicit object CloseIndexHandler extends Handler[CloseIndexRequest, CloseIndexResponse] {
    override def build(request: CloseIndexRequest): ElasticRequest = {
      val endpoint = s"/${request.indexes.values.mkString(",")}/_close"
      ElasticRequest("POST", endpoint)
    }
  }

  implicit object RefreshIndexHandler extends Handler[RefreshIndexRequest, RefreshIndexResponse] {
    override def build(request: RefreshIndexRequest): ElasticRequest = {
      val endpoint = "/" + request.indexes.mkString(",") + "/_refresh"
      ElasticRequest("POST", endpoint)
    }
  }

  implicit object CreateIndexHandler extends Handler[CreateIndexRequest, CreateIndexResponse] {

    override def responseHandler: ResponseHandler[CreateIndexResponse] = new ResponseHandler[CreateIndexResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, CreateIndexResponse] =
        response.statusCode match {
          case 200 | 201 => Right(ResponseHandler.fromResponse[CreateIndexResponse](response))
          case 400 | 500 => Left(ElasticError.parse(response))
          case _         => sys.error(response.toString)
        }
    }

    override def build(request: CreateIndexRequest): ElasticRequest = {

      val endpoint = "/" + ElasticUrlEncoder.encodeUrlFragment(request.name)

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.waitForActiveShards.foreach(params.put("wait_for_active_shards", _))
      request.includeTypeName.foreach(params.put("include_type_name", _))

      val body   = CreateIndexContentBuilder(request).string()
      val entity = HttpEntity(body, "application/json")

      ElasticRequest("PUT", endpoint, params.toMap, entity)
    }
  }

  implicit object DeleteIndexHandler extends Handler[DeleteIndexRequest, DeleteIndexResponse] {

    override def build(request: DeleteIndexRequest): ElasticRequest = {
      val endpoint = "/" + request.indexes.mkString(",")
      ElasticRequest("DELETE", endpoint)
    }
  }

  implicit object UpdateIndexLevelSettingsHandler
      extends Handler[UpdateIndexLevelSettingsRequest, UpdateIndexLevelSettingsResponse] {
    override def build(request: UpdateIndexLevelSettingsRequest): ElasticRequest = {

      val endpoint = "/" + request.indexes.mkString(",") + "/_settings"
      val body     = UpdateIndexLevelSettingsBuilder(request).string()
      val entity   = HttpEntity(body, "application/json")

      ElasticRequest("PUT", endpoint, entity)
    }
  }

  implicit object IndexShardStoreHandler extends Handler[IndexShardStoreRequest, StoreStatusResponse] {

    override def build(request: IndexShardStoreRequest): ElasticRequest = {

      val endpoint = "/" + request.indexes.values.mkString(",") + "/_shard_stores"
      val params   = scala.collection.mutable.Map.empty[String, String]
      request.status.foreach(params.put("status", _))

      ElasticRequest("GET", endpoint, params.toMap)
    }
  }
}

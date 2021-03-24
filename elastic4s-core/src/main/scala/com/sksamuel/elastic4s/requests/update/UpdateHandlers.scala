package com.sksamuel.elastic4s.requests.update

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.{DocumentRef, FetchSourceContextQueryParameterFn, RefreshPolicyHttpValue, Shards}
import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, ElasticUrlEncoder, Handler, HttpEntity, HttpResponse, ResponseHandler, XContentBuilder, XContentFactory}
import com.sksamuel.exts.OptionImplicits._

case class UpdateGet(found: Boolean, _source: Map[String, Any]) // contains the source if specified by the _source parameter

case class UpdateResponse(@JsonProperty("_index") index: String,
                          @JsonProperty("_type") `type`: String,
                          @JsonProperty("_id") id: String,
                          @JsonProperty("_version") version: Long,
                          @JsonProperty("_seq_no") seqNo: Long,
                          @JsonProperty("_primary_term") primaryTerm: Long,
                          result: String,
                          @JsonProperty("forcedRefresh") forcedRefresh: Boolean,
                          @JsonProperty("_shards") shards: Shards,
                          private val get: Option[UpdateGet]) {
  def ref                      = DocumentRef(index, `type`, id)
  def source: Map[String, Any] = get.flatMap(get => Option(get._source)).getOrElse(Map.empty)
  def found: Boolean           = get.forall(_.found)
}

object UpdateByQueryBodyFn {
  def apply(request: UpdateByQueryRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("query", QueryBuilderFn(request.query))
    request.script.map(ScriptBuilderFn.apply).foreach(builder.rawField("script", _))

    request.slice.foreach { slice =>
      builder.startObject("slice")
      builder.field("id", slice.id)
      builder.field("max", slice.max)
      builder.endObject()
    }

    builder.endObject()
  }
}

object UpdateHandlers extends UpdateHandlers

trait UpdateHandlers {

  implicit object UpdateHandler extends Handler[UpdateRequest, UpdateResponse] {

    override def responseHandler: ResponseHandler[UpdateResponse] = new ResponseHandler[UpdateResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, UpdateResponse] = response.statusCode match {
        case 200 | 201 =>
          val json = response.entity.getOrError("Update responses must include a body")
          Right(ResponseHandler.fromEntity[UpdateResponse](json))
        case _ => Left(ElasticError.parse(response))
      }
    }

    override def build(request: UpdateRequest): ElasticRequest = {

      val endpoint =
        s"/${ElasticUrlEncoder.encodeUrlFragment(request.index.index)}/_update/${ElasticUrlEncoder.encodeUrlFragment(request.id)}"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.fetchSource.foreach { context =>
        FetchSourceContextQueryParameterFn(context).foreach { case (key, value) => params.put(key, value) }
      }
      request.retryOnConflict.foreach(params.put("retry_on_conflict", _))
      request.parent.foreach(params.put("parent", _))
      request.routing.foreach(params.put("routing", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.ifPrimaryTerm.map(_.toString).foreach(params.put("if_primary_term", _))
      request.ifSeqNo.map(_.toString).foreach(params.put("if_seq_no", _))
      request.versionType.foreach(params.put("version_type", _))
      request.waitForActiveShards.foreach(params.put("wait_for_active_shards", _))

      val body   = UpdateBuilderFn(request)
      val entity = HttpEntity(body.string(), "application/json")

      ElasticRequest("POST", endpoint, params.toMap, entity)
    }
  }

  implicit object UpdateByQueryHandler extends Handler[UpdateByQueryRequest, UpdateByQueryResponse] {
    override def build(request: UpdateByQueryRequest): ElasticRequest = {

      val endpoint = s"/${request.indexes.values.mkString(",")}/_update_by_query"

      val params = scala.collection.mutable.Map.empty[String, Any]
      if (request.proceedOnConflicts.getOrElse(false))
        params.put("conflicts", "proceed")
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.requestsPerSecond.foreach(params.put("requests_per_second", _))
      request.timeout.map(_.toMillis + "ms").foreach(params.put("timeout", _))
      request.scroll.foreach(params.put("scroll", _))
      request.scrollSize.foreach(params.put("scroll_size", _))
      request.waitForActiveShards.foreach(params.put("wait_for_active_shards", _))
      request.waitForCompletion.foreach(params.put("wait_for_completion", _))
      request.slices.foreach(params.put("slices", _))

      val body = UpdateByQueryBodyFn(request)
      logger.debug(s"Delete by query ${body.string()}")
      val entity = HttpEntity(body.string(), "application/json")

      ElasticRequest("POST", endpoint, params.toMap, entity)
    }
  }
}

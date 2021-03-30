package com.sksamuel.elastic4s.handlers.update

import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s.handlers.common.FetchSourceContextQueryParameterFn
import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.common.RefreshPolicyHttpValue
import com.sksamuel.elastic4s.requests.update.{UpdateByQueryRequest, UpdateByQueryResponse, UpdateRequest, UpdateResponse}
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, ResponseHandler}
import com.sksamuel.exts.OptionImplicits._

import java.net.URLEncoder

object UpdateByQueryBodyFn {
  def apply(request: UpdateByQueryRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("query", queries.QueryBuilderFn(request.query))
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
        case _ => Left(ElasticErrorParser.parse(response))
      }
    }

    override def build(request: UpdateRequest): ElasticRequest = {

      val endpoint =
        s"/${URLEncoder.encode(request.index.index, "UTF-8")}/_update/${URLEncoder.encode(request.id, "UTF-8")}"

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

      val body = UpdateBuilderFn(request)
      val entity = HttpEntity(body.string(), "application/json")

      ElasticRequest("POST", endpoint, params.toMap, entity)
    }
  }

  implicit object UpdateByQueryHandler extends Handler[UpdateByQueryRequest, UpdateByQueryResponse] {

    override def responseHandler: ResponseHandler[UpdateByQueryResponse] = ResponseHandler.default[UpdateByQuerySnakeCase].map { resp =>
      UpdateByQueryResponse(
        resp.took,
        resp.timed_out,
        resp.total,
        resp.updated,
        resp.deleted,
        resp.batches,
        resp.version_conflicts,
        resp.noops,
        resp.throttled_millis,
        resp.requests_per_second,
        resp.throttled_until_millis
      )
    }

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

case class UpdateByQuerySnakeCase(
                                   took: Long,
                                   timed_out: Boolean,
                                   total: Long,
                                   updated: Long,
                                   deleted: Long,
                                   batches: Long,
                                   version_conflicts: Long,
                                   noops: Long,
                                   throttled_millis: Long,
                                   requests_per_second: Long,
                                   throttled_until_millis: Long
                                 )

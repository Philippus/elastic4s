package com.sksamuel.elastic4s.handlers.reindex

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.ext.OptionImplicits.RichOption
import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s.requests.common.{RefreshPolicyHttpValue, Slicing}
import com.sksamuel.elastic4s.requests.reindex.ReindexRequest
import com.sksamuel.elastic4s.requests.task.CreateTaskResponse
import com.sksamuel.elastic4s.{
  BulkIndexByScrollFailure,
  ElasticError,
  ElasticRequest,
  Handler,
  HttpEntity,
  HttpResponse,
  ResponseHandler
}
import scala.concurrent.duration._

case class Retries(bulk: Long, search: Long)

case class ReindexResponse(
    took: Long,
    timed_out: Boolean,
    total: Long,
    updated: Long,
    created: Long,
    deleted: Long,
    batches: Long,
    version_conflicts: Long,
    noops: Long,
    retries: Retries,
    @JsonProperty("throttled_millis") throttledMillis: Long,
    @JsonProperty("requests_per_second") requestsPerSecond: Long,
    @JsonProperty("throttled_until_millis") throttledUntilMillis: Long,
    failures: Option[Seq[BulkIndexByScrollFailure]]
) {
  def throttled: Duration      = throttledMillis.millis
  def throttledUntil: Duration = throttledUntilMillis.millis
}

trait ReindexHandlers {

  implicit object ReindexHandler extends Handler[ReindexRequest, Either[ReindexResponse, CreateTaskResponse]] {

    override def responseHandler: ResponseHandler[Either[ReindexResponse, CreateTaskResponse]] =
      new ResponseHandler[Either[ReindexResponse, CreateTaskResponse]] {
        override def handle(response: HttpResponse): Either[ElasticError, Either[ReindexResponse, CreateTaskResponse]] =
          response.statusCode match {
            case 200 =>
              val entity = response.entity.getOrError("No entity defined but was expected")
              entity.get match {
                case TaskRegex(nodeId, taskId) => Right(Right(CreateTaskResponse(nodeId, taskId)))
                case _                         => Right(Left(ResponseHandler.fromResponse[ReindexResponse](response)))
              }
            case _   => Left(ElasticErrorParser.parse(response))
          }
      }

    override def build(request: ReindexRequest): ElasticRequest = {

      val params = scala.collection.mutable.Map.empty[String, String]
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.waitForCompletion.map(_.toString).foreach(params.put("wait_for_completion", _))
      if (request.waitForActiveShards.getOrElse(-1) > 0)
        params.put("wait_for_active_shards", request.waitForActiveShards.getOrElse(0).toString)
      if (request.requestsPerSecond.getOrElse(-1F) > 0)
        params.put("requests_per_second", request.requestsPerSecond.getOrElse(0).toString)
      request.scroll.foreach(params.put("scroll", _))
      request.slices.foreach(s =>
        if (s == Slicing.AutoSlices) params.put("slices", Slicing.AutoSlicesValue) else params.put("slices", s.toString)
      )

      val body = ReindexBuilderFn(request).string

      ElasticRequest("POST", "/_reindex", params.toMap, HttpEntity(body, "application/json"))
    }
  }
}

package com.sksamuel.elastic4s.requests.reindex

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.RefreshPolicyHttpValue
import com.sksamuel.elastic4s.requests.task.CreateTaskResponse
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, ResponseHandler}
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration._

case class Retries(bulk: Long, search: Long)

case class ReindexFailure()

case class ReindexResponse(took: Long,
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
                           failures: Seq[ReindexFailure]) {
  def throttled: Duration = throttledMillis.millis
  def throttledUntil: Duration = throttledUntilMillis.millis
}

trait ReindexHandlers {

  implicit object ReindexHandler extends Handler[ReindexRequest, Either[ReindexResponse, CreateTaskResponse]] {

    private val TaskRegex = """\{"task":"(.*):(.*)"\}""".r

    override def responseHandler: ResponseHandler[Either[ReindexResponse, CreateTaskResponse]] = new ResponseHandler[Either[ReindexResponse, CreateTaskResponse]] {
      override def handle(response: HttpResponse): Either[ElasticError, Either[ReindexResponse, CreateTaskResponse]] = response.statusCode match {
        case 200 =>
          val entity = response.entity.getOrError("No entity defined but was expected")
          entity.get match {
            case TaskRegex(nodeId, taskId) => Right(Right(CreateTaskResponse(nodeId, taskId)))
            case _ => Right(Left(ResponseHandler.fromResponse[ReindexResponse](response)))
          }
        case _ => Left(ElasticError.parse(response))
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

      val body = ReindexBuilderFn(request).string()

      ElasticRequest("POST", "/_reindex", params.toMap, HttpEntity(body, "application/json"))
    }
  }
}

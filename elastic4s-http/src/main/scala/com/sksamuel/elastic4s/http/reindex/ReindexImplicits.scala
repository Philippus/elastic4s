package com.sksamuel.elastic4s.http.reindex

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.reindex.ReindexDefinition
import org.apache.http.entity.ContentType

import scala.concurrent.duration._

case class Retries(bulk: Long,
                   search: Long)

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

trait ReindexImplicits {

  implicit object ReindexHttpExecutable extends HttpExecutable[ReindexDefinition, ReindexResponse] {

    override def responseHandler = new ResponseHandler[ReindexResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, ReindexResponse] = response.statusCode match {
        case 200 => Right(ResponseHandler.fromResponse[ReindexResponse](response))
        case _ => Left(ElasticError.parse(response))
      }
    }

    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient, request: ReindexDefinition): F[HttpResponse] = {

      val params = scala.collection.mutable.Map.empty[String, String]
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      if (request.waitForCompletion.getOrElse(false))
        params.put("wait_for_completion", "true")
      if (request.waitForActiveShards.getOrElse(-1) > 0)
        params.put("wait_for_active_shards", request.waitForActiveShards.getOrElse(0).toString)
      if (request.requestsPerSecond.getOrElse(-1F) > 0)
        params.put("requests_per_second", request.requestsPerSecond.getOrElse(0).toString)

      val body = ReindexBuilderFn(request).string()

      client.async("POST", "_reindex", params.toMap, HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType))
    }
  }
}

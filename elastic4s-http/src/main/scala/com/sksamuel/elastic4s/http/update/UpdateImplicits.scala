package com.sksamuel.elastic4s.http.update

import cats.Show
import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.http.index.ElasticError
import com.sksamuel.elastic4s.http.values.{RefreshPolicyHttpValue, Shards}
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.update.{UpdateByQueryDefinition, UpdateDefinition}
import com.sksamuel.exts.OptionImplicits._
import org.apache.http.entity.ContentType

import scala.concurrent.Future

case class UpdateResponse(@JsonProperty("_index") index: String,
                          @JsonProperty("_type") `type`: String,
                          @JsonProperty("_id") id: String,
                          @JsonProperty("_version") version: Long,
                          result: String,
                          @JsonProperty("forcedRefresh") forcedRefresh: Boolean,
                          @JsonProperty("_shards") shards: Shards) {
  def ref = DocumentRef(index, `type`, id)
}

object UpdateByQueryBodyFn {
  def apply(request: UpdateByQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("query", QueryBuilderFn(request.query))
    request.script.map(ScriptBuilderFn.apply).foreach(builder.rawField("script", _))
    builder.endObject()
    builder
  }
}

case class RequestFailure(error: ElasticError, status: Int)

object UpdateImplicits extends UpdateImplicits

trait UpdateImplicits {

  implicit object UpdateShow extends Show[UpdateDefinition] {
    override def show(f: UpdateDefinition): String = UpdateBuilderFn(f).string()
  }

  implicit object UpdateByQueryShow extends Show[UpdateByQueryDefinition] {
    override def show(req: UpdateByQueryDefinition): String = UpdateByQueryBodyFn(req).string()
  }

  implicit object UpdateHttpExecutable extends HttpExecutable[UpdateDefinition, Either[RequestFailure, UpdateResponse]] {

    override def responseHandler = new ResponseHandler[Either[RequestFailure, UpdateResponse]] {
      override def doit(response: HttpResponse): Either[RequestFailure, UpdateResponse] = response.statusCode match {
        case 200 | 201 => Right(ResponseHandler.fromEntity[UpdateResponse](response.entity.getOrError("Create index responses must have a body")))
        case _ => Left(ResponseHandler.fromEntity[RequestFailure](response.entity.get))
      }
    }

    override def execute(client: HttpRequestClient, request: UpdateDefinition): Future[HttpResponse] = {

      val endpoint = s"/${request.indexAndTypes.index}/${request.indexAndTypes.types.mkString(",")}/${request.id}/_update"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.fetchSource.foreach { context =>
        if (!context.fetchSource) params.put("_source", "false")
      }
      request.retryOnConflict.foreach(params.put("retry_on_conflict", _))
      request.parent.foreach(params.put("parent", _))
      request.routing.foreach(params.put("routing", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.foreach(params.put("version_type", _))
      request.waitForActiveShards.foreach(params.put("wait_for_active_shards", _))

      val body = UpdateBuilderFn(request)
      val entity = HttpEntity(body.string, ContentType.APPLICATION_JSON.getMimeType)

      client.async("POST", endpoint, params.toMap, entity)
    }
  }

  implicit object UpdateByQueryHttpExecutable extends HttpExecutable[UpdateByQueryDefinition, UpdateByQueryResponse] {
    override def execute(client: HttpRequestClient, request: UpdateByQueryDefinition): Future[HttpResponse] = {

      val endpoint = if (request.indexesAndTypes.types.isEmpty)
        s"/${request.indexesAndTypes.indexes.mkString(",")}/_update_by_query"
      else
        s"/${request.indexesAndTypes.indexes.mkString(",")}/${request.indexesAndTypes.types.mkString(",")}/_update_by_query"

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.proceedOnConflicts.contains(true)) {
        params.put("conflicts", "proceed")
      }
      request.requestsPerSecond.map(_.toString).foreach(params.put("requests_per_second", _))
      request.timeout.map(_.toMillis + "ms").foreach(params.put("timeout", _))
      request.scrollSize.map(_.toString).foreach(params.put("scroll_size", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))

      val body = UpdateByQueryBodyFn(request)
      logger.debug(s"Delete by query ${body.string}")
      val entity = HttpEntity(body.string, ContentType.APPLICATION_JSON.getMimeType)

      client.async("POST", endpoint, params.toMap, entity)
    }
  }
}

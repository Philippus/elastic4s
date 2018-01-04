package com.sksamuel.elastic4s.http.update

import java.net.URLEncoder

import cats.Show
import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.update.{UpdateByQueryDefinition, UpdateDefinition}
import com.sksamuel.exts.OptionImplicits._
import org.apache.http.entity.ContentType

case class UpdateGet(found: Boolean, _source: Map[String, Any]) // contains the source if specified by the _source parameter

case class UpdateResponse(@JsonProperty("_index") index: String,
                          @JsonProperty("_type") `type`: String,
                          @JsonProperty("_id") id: String,
                          @JsonProperty("_version") version: Long,
                          result: String,
                          @JsonProperty("forcedRefresh") forcedRefresh: Boolean,
                          @JsonProperty("_shards") shards: Shards,
                          private val get: Option[UpdateGet]
                         ) {
  def ref = DocumentRef(index, `type`, id)
  def source: Map[String, Any] = get.flatMap(get => Option(get._source)).getOrElse(Map.empty)
  def found: Boolean = get.forall(_.found)
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

object UpdateImplicits extends UpdateImplicits

trait UpdateImplicits {

  implicit object UpdateShow extends Show[UpdateDefinition] {
    override def show(f: UpdateDefinition): String = UpdateBuilderFn(f).string()
  }

  implicit object UpdateByQueryShow extends Show[UpdateByQueryDefinition] {
    override def show(req: UpdateByQueryDefinition): String = UpdateByQueryBodyFn(req).string()
  }

  implicit object UpdateHttpExecutable extends HttpExecutable[UpdateDefinition, UpdateResponse] {

    override def responseHandler = new ResponseHandler[UpdateResponse] {
      override def handle(response: HttpResponse) = response.statusCode match {
        case 200 | 201 =>
          val json = response.entity.getOrError("Update responses must include a body")
          Right(ResponseHandler.fromEntity[UpdateResponse](json))
        case _ => Left(ElasticError.parse(response))
      }
    }

    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient, request: UpdateDefinition): F[HttpResponse] = {

      val endpoint = s"/${URLEncoder.encode(request.indexAndType.index)}/${request.indexAndType.`type`}/${URLEncoder.encode(request.id)}/_update"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.fetchSource.foreach { context =>
        FetchSourceContextQueryParameterFn(context).foreach { case (key, value) => params.put(key, value) }
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
    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient, request: UpdateByQueryDefinition): F[HttpResponse] = {

      val endpoint = if (request.indexesAndTypes.types.isEmpty)
        s"/${request.indexesAndTypes.indexes.mkString(",")}/_update_by_query"
      else
        s"/${request.indexesAndTypes.indexes.mkString(",")}/${request.indexesAndTypes.types.mkString(",")}/_update_by_query"

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.proceedOnConflicts.getOrElse(false)) {
        params.put("conflicts", "proceed")
      }
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
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

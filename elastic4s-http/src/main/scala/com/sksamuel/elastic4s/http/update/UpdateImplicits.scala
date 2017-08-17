package com.sksamuel.elastic4s.http.update

import cats.Show
import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.http.values.{RefreshPolicyHttpValue, Shards}
import com.sksamuel.elastic4s.http.{HttpEntity, HttpExecutable, HttpRequestClient, HttpResponse, ResponseHandler}
import com.sksamuel.elastic4s.update.UpdateDefinition
import com.sksamuel.exts.Logging
import org.elasticsearch.client.http.entity.ContentType

import scala.concurrent.Future
import scala.util.Try

case class UpdateResponse(@JsonProperty("_index") index: String,
                          @JsonProperty("_type") `type`: String,
                          @JsonProperty("_id") id: String,
                          @JsonProperty("_version") version: Long,
                          result: String,
                          @JsonProperty("forcedRefresh") forcedRefresh: Boolean,
                          @JsonProperty("_shards") shards: Shards) {
  def ref = DocumentRef(index, `type`, id)
}

object UpdateImplicits extends UpdateImplicits

trait UpdateImplicits {

  implicit object UpdateShow extends Show[UpdateDefinition] {
    override def show(f: UpdateDefinition): String = UpdateBuilderFn(f).string()
  }

  implicit object UpdateHttpExecutable extends HttpExecutable[UpdateDefinition, UpdateResponse] with Logging {

    override def responseHandler: ResponseHandler[UpdateResponse] = new ResponseHandler[UpdateResponse] {
      override def handle(response: HttpResponse): Try[UpdateResponse] = response.statusCode match {
        case 404 => sys.error("Error updating")
        case _ => Try {
          ResponseHandler.fromEntity[UpdateResponse](response.entity.get)
        }
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
}

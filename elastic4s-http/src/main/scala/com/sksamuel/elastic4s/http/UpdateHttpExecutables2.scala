package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.JsonFormat
import com.sksamuel.elastic4s.http.update.{UpdateContentBuilder, UpdateResponse}
import com.sksamuel.elastic4s.update.UpdateDefinition
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

object UpdateHttpExecutables2 {

  implicit def apply(implicit format: JsonFormat[UpdateResponse]) = new HttpExec2[UpdateDefinition, UpdateResponse] {

    override def execute(client: RestClient,
                         request: UpdateDefinition): Future[UpdateResponse] = {

      val method = "POST"
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

      val body = UpdateContentBuilder(request)
      val entity = new StringEntity(body.string, ContentType.APPLICATION_JSON)

      client.future(method, endpoint, params.toMap, entity, ResponseHandler.default(format))
    }
  }
}

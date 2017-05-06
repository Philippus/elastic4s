package com.sksamuel.elastic4s.http.update

import cats.Show
import com.sksamuel.elastic4s.http.{HttpExecutable, RefreshPolicyHttpValue, ResponseHandler, Shards}
import com.sksamuel.elastic4s.update.UpdateDefinition
import com.sksamuel.exts.Logging
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

case class UpdateResponse(_index: String,
                          _type: String,
                          _id: String,
                          _version: Long,
                          result: String,
                          forced_refresh: Boolean,
                          _shards: Shards)

object UpdateImplicits extends UpdateImplicits

trait UpdateImplicits {

  implicit object UpdateShow extends Show[UpdateDefinition] {
    override def show(f: UpdateDefinition): String = UpdateContentBuilder(f).string()
  }

  implicit object UpdateHttpExecutable extends HttpExecutable[UpdateDefinition, UpdateResponse] with Logging {

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
      logger.debug(s"Update Entity: ${body.string}")

      client.async(method, endpoint, params.toMap, entity, ResponseHandler.default)
    }
  }
}

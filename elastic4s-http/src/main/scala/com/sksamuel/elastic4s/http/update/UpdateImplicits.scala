package com.sksamuel.elastic4s.http.update

import cats.Show
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.update.{UpdateByQueryDefinition, UpdateDefinition}
import com.sksamuel.exts.Logging
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

import scala.concurrent.Future

case class UpdateResponse(_index: String,
                          _type: String,
                          _id: String,
                          _version: Long,
                          result: String,
                          forced_refresh: Boolean,
                          _shards: Shards)

object UpdateByQueryBodyFn {
  def apply(request: UpdateByQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.rawField("query", QueryBuilderFn(request.query).bytes, XContentType.JSON)
    request.script.map(ScriptBuilderFn.apply).map(_.bytes)
      .foreach(builder.rawField("script", _, XContentType.JSON))
    builder.endObject()
    builder
  }
}

object UpdateImplicits extends UpdateImplicits

trait UpdateImplicits {

  implicit object UpdateShow extends Show[UpdateDefinition] {
    override def show(f: UpdateDefinition): String = UpdateContentBuilder(f).string()
  }

  implicit object UpdateByQueryShow extends Show[UpdateByQueryDefinition] {
    override def show(req: UpdateByQueryDefinition): String = UpdateByQueryBodyFn(req).string()
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

  implicit object UpdateByQueryHttpExecutable extends HttpExecutable[UpdateByQueryDefinition, UpdateByQueryResponse] {
    override def execute(client: RestClient, request: UpdateByQueryDefinition): Future[UpdateByQueryResponse] = {

      val endpoint = if (request.indexesAndTypes.types.isEmpty)
        s"/${request.indexesAndTypes.indexes.mkString(",")}/_update_by_query"
      else
        s"/${request.indexesAndTypes.indexes.mkString(",")}/${request.indexesAndTypes.types.mkString(",")}/_update_by_query"

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.abortOnVersionConflict.contains(true)) {
        params.put("conflicts", "proceed")
      }
      request.requestsPerSecond.map(_.toString).foreach(params.put("requests_per_second", _))
      request.timeout.map(_.toMillis + "ms").foreach(params.put("timeout", _))
      request.scrollSize.map(_.toString).foreach(params.put("scroll_size", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))

      val body = UpdateByQueryBodyFn(request)
      logger.debug(s"Update by query ${body.string}")
      val entity = new StringEntity(body.string, ContentType.APPLICATION_JSON)

      client.async("POST", endpoint, params.toMap, entity, ResponseHandler.default)
    }
  }
}

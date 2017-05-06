package com.sksamuel.elastic4s.http.delete

import cats.Show
import com.sksamuel.elastic4s.delete.{DeleteByIdDefinition, DeleteByQueryDefinition}
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.http.{HttpExecutable, RefreshPolicyHttpValue, ResponseHandler}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

import scala.concurrent.Future

object DeleteByQueryBodyFn {
  def apply(request: DeleteByQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.rawField("query", QueryBuilderFn(request.query).bytes, XContentType.JSON)
    builder.endObject()
    builder
  }
}

trait DeleteImplicits {

  implicit object DeleteByQueryShow extends Show[DeleteByQueryDefinition] {
    override def show(req: DeleteByQueryDefinition): String = DeleteByQueryBodyFn(req).string()
  }

  implicit object DeleteByQueryExecutable extends HttpExecutable[DeleteByQueryDefinition, DeleteByQueryResponse] {

    override def execute(client: RestClient, request: DeleteByQueryDefinition): Future[DeleteByQueryResponse] = {

      val endpoint = if (request.indexesAndTypes.types.isEmpty)
        s"/${request.indexesAndTypes.indexes.mkString(",")}/_delete_by_query"
      else
        s"/${request.indexesAndTypes.indexes.mkString(",")}/${request.indexesAndTypes.types.mkString(",")}/_delete_by_query"

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.abortOnVersionConflict.contains(true)) {
        params.put("conflicts", "proceed")
      }
      request.requestsPerSecond.map(_.toString).foreach(params.put("requests_per_second", _))
      request.timeout.map(_.toMillis + "ms").foreach(params.put("timeout", _))
      request.scrollSize.map(_.toString).foreach(params.put("scroll_size", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))

      val body = DeleteByQueryBodyFn(request)
      logger.debug(s"Delete by query ${body.string}")
      val entity = new StringEntity(body.string, ContentType.APPLICATION_JSON)

      client.async("POST", endpoint, params.toMap, entity, ResponseHandler.default)
    }
  }

  implicit object DeleteByIdExecutable extends HttpExecutable[DeleteByIdDefinition, DeleteResponse] {

    override def execute(client: RestClient,
                         request: DeleteByIdDefinition): Future[DeleteResponse] = {

      val method = "DELETE"
      val endpoint = s"/${request.indexType.index}/${request.indexType.`type`}/${request.id}"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.parent.foreach(params.put("parent", _))
      request.routing.foreach(params.put("routing", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.map(_.name).foreach(params.put("versionType", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))

      client.async(method, endpoint, params.toMap, ResponseHandler.failure404)
    }
  }
}

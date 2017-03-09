package com.sksamuel.elastic4s.http.delete

import cats.Show
import com.sksamuel.elastic4s.delete.{DeleteByIdDefinition, DeleteByQueryDefinition}
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.http.{HttpExecutable, RefreshPolicyHttpValue, Shards}
import com.sksamuel.elastic4s.{DocumentRef, JsonFormat}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

import scala.collection.JavaConverters._
import scala.concurrent.Future

case class DeleteByQueryResponse(
                                  took: Long,
                                  timed_out: Boolean,
                                  total: Long,
                                  deleted: Long,
                                  batches: Long,
                                  version_conflicts: Long,
                                  noops: Long,
                                  throttled_millis: Long,
                                  requests_per_second: Long,
                                  throttled_until_millis: Long
                                )

case class DeleteResponse(_shards: Shards,
                          found: Boolean,
                          private val _index: String,
                          private val _type: String,
                          private val _id: String,
                          private val _version: Long,
                          result: String) {
  def index: String = _index
  def `type`: String = _type
  def id: String = _id
  def version: Long = _version
  def ref = DocumentRef(index, `type`, id)
}

object DeleteByQueryBodyFn {
  def apply(request: DeleteByQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.rawField("query", QueryBuilderFn(request.query).bytes())
    builder.endObject()
    builder
  }
}

trait DeleteImplicits {

  implicit object DeleteByQueryShow extends Show[DeleteByQueryDefinition] {
    override def show(req: DeleteByQueryDefinition): String = DeleteByQueryBodyFn(req).string()
  }

  implicit object DeleteByQueryExecutable extends HttpExecutable[DeleteByQueryDefinition, DeleteByQueryResponse] {

    override def execute(client: RestClient,
                         request: DeleteByQueryDefinition,
                         format: JsonFormat[DeleteByQueryResponse]): Future[DeleteByQueryResponse] = {

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

      executeAsyncAndMapResponse(client.performRequestAsync("POST", endpoint, params.asJava, entity, _), format)
    }
  }

  implicit object DeleteByIdExecutable extends HttpExecutable[DeleteByIdDefinition, DeleteResponse] {

    override def execute(client: RestClient,
                         request: DeleteByIdDefinition,
                         format: JsonFormat[DeleteResponse]): Future[DeleteResponse] = {

      val method = "DELETE"
      val url = s"/${request.indexType.index}/${request.indexType.`type`}/${request.id}"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.parent.foreach(params.put("parent", _))
      request.routing.foreach(params.put("routing", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.map(_.name).foreach(params.put("versionType", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))

      executeAsyncAndMapResponse(client.performRequestAsync(method, url, params.asJava, _), format)
    }
  }
}

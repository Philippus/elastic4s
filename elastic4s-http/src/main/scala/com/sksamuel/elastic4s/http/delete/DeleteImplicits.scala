package com.sksamuel.elastic4s.http.delete

import cats.Show
import com.sksamuel.elastic4s.delete.{DeleteByIdDefinition, DeleteByQueryDefinition}
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.http.update.RequestFailure
import com.sksamuel.elastic4s.http.values.RefreshPolicyHttpValue
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.exts.OptionImplicits._
import org.apache.http.entity.ContentType

import scala.concurrent.Future

object DeleteByQueryBodyFn {
  def apply(request: DeleteByQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("query", QueryBuilderFn(request.query))
    builder.endObject()
    builder
  }
}

trait DeleteImplicits {

  implicit object DeleteByQueryShow extends Show[DeleteByQueryDefinition] {
    override def show(req: DeleteByQueryDefinition): String = DeleteByQueryBodyFn(req).string()
  }

  implicit object DeleteByQueryExecutable extends HttpExecutable[DeleteByQueryDefinition, Either[RequestFailure, DeleteByQueryResponse]] {

    override def responseHandler = new ResponseHandler[Either[RequestFailure, DeleteByQueryResponse]] {
      override def doit(response: HttpResponse): Either[RequestFailure, DeleteByQueryResponse] = response.statusCode match {
        case 200 | 201 => Right(ResponseHandler.fromEntity[DeleteByQueryResponse](response.entity.getOrError("Create index responses must have a body")))
        case _ => Left(ResponseHandler.fromEntity[RequestFailure](response.entity.get))
      }
    }

    override def execute(client: HttpRequestClient, request: DeleteByQueryDefinition): Future[HttpResponse] = {

      val endpoint = if (request.indexesAndTypes.types.isEmpty)
        s"/${request.indexesAndTypes.indexes.mkString(",")}/_all/_delete_by_query"
      else
        s"/${request.indexesAndTypes.indexes.mkString(",")}/${request.indexesAndTypes.types.head}/_delete_by_query"

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.proceedOnConflicts.getOrElse(false)) {
        params.put("conflicts", "proceed")
      }
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.requestsPerSecond.map(_.toString).foreach(params.put("requests_per_second", _))
      request.timeout.map(_.toMillis + "ms").foreach(params.put("timeout", _))
      request.scrollSize.map(_.toString).foreach(params.put("scroll_size", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))

      val body = DeleteByQueryBodyFn(request)
      logger.debug(s"Delete by query ${body.string}")
      val entity = HttpEntity(body.string, ContentType.APPLICATION_JSON.getMimeType)

      client.async("POST", endpoint, params.toMap, entity)
    }
  }

  implicit object DeleteByIdExecutable extends HttpExecutable[DeleteByIdDefinition, Either[RequestFailure, DeleteResponse]] {

    override def responseHandler = new ResponseHandler[Either[RequestFailure, DeleteResponse]] {
      override def doit(response: HttpResponse): Either[RequestFailure, DeleteResponse] = {
        def right = Right(ResponseHandler.fromEntity[DeleteResponse](response.entity.getOrError("Delete responses must have a body")))
        def left = Left(ResponseHandler.fromEntity[RequestFailure](response.entity.get))
        response.statusCode match {
          case 200 | 201 => right
          // annoying, 404s can return different types of data for a delete
          case 404 =>
            val node = ResponseHandler.json(response.entity.get)
            if (node.has("error")) left else right
          case _ => left
        }
      }
    }

    override def execute(client: HttpRequestClient, request: DeleteByIdDefinition): Future[HttpResponse] = {

      val method = "DELETE"
      val endpoint = s"/${request.indexType.index}/${request.indexType.`type`}/${request.id}"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.parent.foreach(params.put("parent", _))
      request.routing.foreach(params.put("routing", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.map(EnumConversions.versionType).foreach(params.put("versionType", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))

      client.async(method, endpoint, params.toMap)
    }
  }
}

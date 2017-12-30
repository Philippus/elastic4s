package com.sksamuel.elastic4s.http.delete

import java.net.URLEncoder

import cats.{Functor, Show}
import com.sksamuel.elastic4s.delete.{DeleteByIdDefinition, DeleteByQueryDefinition}
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import org.apache.http.entity.ContentType

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

  implicit object DeleteByQueryExecutable extends HttpExecutable[DeleteByQueryDefinition, DeleteByQueryResponse] {

    override def responseHandler = new ResponseHandler[DeleteByQueryResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, DeleteByQueryResponse] = response.statusCode match {
        case 200 | 201 => Right(ResponseHandler.fromResponse[DeleteByQueryResponse](response))
        case _ => Left(ElasticError.parse(response))
      }
    }

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: DeleteByQueryDefinition): F[HttpResponse] = {

      val endpoint = if (request.indexesAndTypes.types.isEmpty)
        s"/${request.indexesAndTypes.indexes.map(URLEncoder.encode).mkString(",")}/_all/_delete_by_query"
      else
        s"/${request.indexesAndTypes.indexes.map(URLEncoder.encode).mkString(",")}/${request.indexesAndTypes.types.head}/_delete_by_query"

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

  implicit object DeleteByIdExecutable extends HttpExecutable[DeleteByIdDefinition, DeleteResponse] {

    override def responseHandler = new ResponseHandler[DeleteResponse] {

      override def handle(response: HttpResponse): Either[ElasticError, DeleteResponse] = {

        def right = Right(ResponseHandler.fromResponse[DeleteResponse](response))

        def left = Left(ElasticError.parse(response))
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

    override def execute[F[_]: FromListener: Functor](client: HttpRequestClient, request: DeleteByIdDefinition): F[HttpResponse] = {

      val method = "DELETE"
      val endpoint = s"/${URLEncoder.encode(request.indexType.index)}/${request.indexType.`type`}/${URLEncoder.encode(request.id.toString)}"

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

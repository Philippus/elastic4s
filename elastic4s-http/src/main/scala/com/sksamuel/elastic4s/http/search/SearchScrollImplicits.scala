package com.sksamuel.elastic4s.http.search

import cats.{Functor, Show}
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.{ClearScrollDefinition, SearchScrollDefinition}
import com.sksamuel.exts.OptionImplicits._
import org.apache.http.entity.ContentType

case class ClearScrollResponse(succeeded: Boolean, num_freed: Int)

trait SearchScrollImplicits {

  implicit object SearchScrollShow extends Show[SearchScrollDefinition] {
    override def show(req: SearchScrollDefinition): String = SearchScrollBuilderFn(req).string
  }

  implicit object ClearScrollHttpExec extends HttpExecutable[ClearScrollDefinition, ClearScrollResponse] {

    override def responseHandler = new ResponseHandler[ClearScrollResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, ClearScrollResponse] = response.statusCode match {
        case 200 =>
          Right(ResponseHandler.fromResponse[ClearScrollResponse](response))
        case _ =>
          Left(ElasticError.parse(response))
      }
    }

    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient, request: ClearScrollDefinition): F[HttpResponse] = {

      val (method, endpoint) = ("DELETE", s"/_search/scroll/")

      val body = ClearScrollContentFn(request).string()
      logger.debug("Executing clear scroll: " + body)
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)

      client.async(method, endpoint, Map.empty, entity)
    }
  }

  implicit object SearchScrollHttpExecutable extends HttpExecutable[SearchScrollDefinition, SearchResponse] {

    override def responseHandler = new ResponseHandler[SearchResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, SearchResponse] = response.statusCode match {
        case 200 => Right(ResponseHandler.fromResponse[SearchResponse](response))
        case _ => Left(ElasticError.parse(response))
      }
    }

    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient, req: SearchScrollDefinition): F[HttpResponse] = {

      val body = SearchScrollBuilderFn(req).string()
      logger.debug("Executing search scroll: " + body)
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)

      client.async("POST", "/_search/scroll", Map.empty, entity)
    }
  }
}

object SearchScrollBuilderFn {
  def apply(req: SearchScrollDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    req.keepAlive.foreach(builder.field("scroll", _))
    builder.field("scroll_id", req.id)
    builder.endObject()
  }
}

object ClearScrollContentFn {
  def apply(req: ClearScrollDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.array("scroll_id", req.ids.toArray)
    builder.endObject()
  }
}

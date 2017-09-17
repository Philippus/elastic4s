package com.sksamuel.elastic4s.http.search

import cats.Show
import com.sksamuel.elastic4s.http.update.RequestFailure
import com.sksamuel.elastic4s.http.{HttpEntity, HttpExecutable, HttpRequestClient, HttpResponse, ResponseHandler}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.{ClearScrollDefinition, SearchScrollDefinition}
import org.apache.http.entity.ContentType
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.Future

case class ClearScrollResponse(succeeded: Boolean, num_freed: Int)

trait SearchScrollImplicits {

  implicit object SearchScrollShow extends Show[SearchScrollDefinition] {
    override def show(req: SearchScrollDefinition): String = SearchScrollBuilderFn(req).string
  }

  implicit object ClearScrollHttpExec extends HttpExecutable[ClearScrollDefinition, Either[RequestFailure, ClearScrollResponse]] {

    override def responseHandler = new ResponseHandler[Either[RequestFailure, ClearScrollResponse]] {
      override def doit(response: HttpResponse): Either[RequestFailure, ClearScrollResponse] = response.statusCode match {
        case 200 => Right(ResponseHandler.fromEntity[ClearScrollResponse](response.entity.getOrError("No entity defined")))
        case _ => Left(ResponseHandler.fromEntity[RequestFailure](response.entity.get))
      }
    }

    override def execute(client: HttpRequestClient, request: ClearScrollDefinition): Future[HttpResponse] = {

      val (method, endpoint) = ("DELETE", s"/_search/scroll/")

      val body = ClearScrollContentFn(request).string()
      logger.debug("Executing clear scroll: " + body)
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)

      client.async(method, endpoint, Map.empty, entity)
    }
  }

  implicit object SearchScrollHttpExecutable extends HttpExecutable[SearchScrollDefinition, Either[RequestFailure, SearchResponse]] {

    override def responseHandler = new ResponseHandler[Either[RequestFailure, SearchResponse]] {
      override def doit(response: HttpResponse): Either[RequestFailure, SearchResponse] = response.statusCode match {
        case 200 => Right(ResponseHandler.fromEntity[SearchResponse](response.entity.getOrError("No entity defined")))
        case _ => Left(ResponseHandler.fromEntity[RequestFailure](response.entity.get))
      }
    }

    override def execute(client: HttpRequestClient, req: SearchScrollDefinition): Future[HttpResponse] = {

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

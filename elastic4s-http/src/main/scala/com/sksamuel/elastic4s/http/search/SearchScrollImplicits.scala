package com.sksamuel.elastic4s.http.search

import cats.Show
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.{ClearScrollDefinition, SearchScrollDefinition}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

case class ClearScrollResponse(succeeded: Boolean, num_freed: Int)

trait SearchScrollImplicits {

  implicit object SearchScrollShow extends Show[SearchScrollDefinition] {
    override def show(req: SearchScrollDefinition): String = SearchScrollContentFn(req).string
  }

  implicit object ClearScrollHttpExec extends HttpExecutable[ClearScrollDefinition, ClearScrollResponse] {
    override def execute(client: RestClient,
                         request: ClearScrollDefinition): Future[ClearScrollResponse] = {

      val (method, endpoint) = ("DELETE", s"/_search/scroll/")

      val body = ClearScrollContentFn(request).string()
      logger.debug("Executing clear scroll: " + body)
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.async(method, endpoint, Map.empty, entity, ResponseHandler.default)
    }
  }

  implicit object SearchScrollHttpExecutable extends HttpExecutable[SearchScrollDefinition, SearchResponse] {

    override def execute(client: RestClient,
                         req: SearchScrollDefinition): Future[SearchResponse] = {

      val body = SearchScrollContentFn(req).string()
      logger.debug("Executing search scroll: " + body)
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.async( "POST", "/_search/scroll", Map.empty, entity, ResponseHandler.default)
    }
  }
}

object SearchScrollContentFn {
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
    builder.field("scroll_id", req.ids.toArray)
    builder.endObject()
  }
}

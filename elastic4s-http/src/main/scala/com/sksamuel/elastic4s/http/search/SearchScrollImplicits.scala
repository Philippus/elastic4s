package com.sksamuel.elastic4s.http.search

import java.util

import cats.Show
import com.sksamuel.elastic4s.JsonFormat
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.searches.SearchScrollDefinition
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

import scala.concurrent.Future

trait SearchScrollImplicits {

  implicit object SearchScrollShow extends Show[SearchScrollDefinition] {
    override def show(req: SearchScrollDefinition): String = SearchScrollContentFn(req).string()
  }

  implicit object SearchScrollHttpExecutable extends HttpExecutable[SearchScrollDefinition, SearchResponse] {

    private val endpoint = "/_search/scroll"
    private val method = "POST"

    override def execute(client: RestClient,
                         req: SearchScrollDefinition,
                         format: JsonFormat[SearchResponse]): Future[SearchResponse] = {

      val body = SearchScrollContentFn(req).string()
      logger.debug("Executing search scroll: " + body)
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      executeAsyncAndMapResponse(client.performRequestAsync(method, endpoint, new util.HashMap[String, String], entity, _), format)
    }
  }
}

object SearchScrollContentFn {
  def apply(req: SearchScrollDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    req.keepAlive.foreach(builder.field("scroll", _))
    builder.field("scroll_id", req.id)
    builder.endObject()
  }
}

package com.sksamuel.elastic4s.http.count

import java.net.URLEncoder

import com.sksamuel.elastic4s.count.CountDefinition
import com.sksamuel.elastic4s.http.{HttpEntity, HttpExecutable, HttpRequestClient, HttpResponse}
import org.apache.http.entity.ContentType

import scala.concurrent.Future

case class CountResponse(count: Long)

trait CountImplicits {

  implicit object CountHttpExecutable extends HttpExecutable[CountDefinition, CountResponse] {

    override def execute(client: HttpRequestClient, request: CountDefinition): Future[HttpResponse] = {

      val endpoint = if (request.indexes.isEmpty && request.types.isEmpty)
        "/_count"
      else if (request.indexes.isEmpty)
        "/_all/" + request.types.map(URLEncoder.encode).mkString(",") + "/_count"
      else if (request.types.isEmpty)
        "/" + request.indexes.values.map(URLEncoder.encode).mkString(",") + "/_count"
      else
        "/" + request.indexes.values.map(URLEncoder.encode).mkString(",") + "/" + request.types.map(URLEncoder.encode).mkString(",") + "/_count"

      val builder = CountBodyBuilderFn(request)
      val body = builder.string()

      client.async("GET", endpoint, Map.empty, HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType))
    }
  }
}

package com.sksamuel.elastic4s.http.count

import java.net.URLEncoder
import java.nio.charset.Charset

import com.sksamuel.elastic4s.count.CountRequest
import com.sksamuel.elastic4s.http._
import org.apache.http.entity.ContentType

case class CountResponse(count: Long)

trait CountHandlers {

  implicit object CountHandler extends Handler[CountRequest, CountResponse] {

    override def build(request: CountRequest): ElasticRequest = {

      val endpoint =
        if (request.indexes.isEmpty && request.types.isEmpty)
          "/_count"
        else if (request.indexes.isEmpty)
          "/_all/" + request.types.map(URLEncoder.encode(_, Charset.defaultCharset())).mkString(",") + "/_count"
        else if (request.types.isEmpty)
          "/" + request.indexes.values.map(URLEncoder.encode(_, Charset.defaultCharset())).mkString(",") + "/_count"
        else
          "/" + request.indexes.values.map(URLEncoder.encode(_, Charset.defaultCharset())).mkString(",") + "/" + request.types
            .map(URLEncoder.encode(_, Charset.defaultCharset()))
            .mkString(",") + "/_count"

      val builder = CountBodyBuilderFn(request)
      val body    = builder.string()

      ElasticRequest("GET", endpoint, HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType))
    }
  }
}

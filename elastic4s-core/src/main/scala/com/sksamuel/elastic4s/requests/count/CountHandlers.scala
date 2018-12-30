package com.sksamuel.elastic4s.requests.count

import java.net.URLEncoder

import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity}

case class CountResponse(count: Long)

trait CountHandlers {

  implicit object CountHandler extends Handler[CountRequest, CountResponse] {

    override def build(request: CountRequest): ElasticRequest = {

      val endpoint =
        if (request.indexes.isEmpty && request.types.isEmpty)
          "/_count"
        else if (request.indexes.isEmpty)
          "/_all/" + request.types.map(URLEncoder.encode).mkString(",") + "/_count"
        else if (request.types.isEmpty)
          "/" + request.indexes.values.map(URLEncoder.encode).mkString(",") + "/_count"
        else
          "/" + request.indexes.values.map(URLEncoder.encode).mkString(",") + "/" + request.types
            .map(URLEncoder.encode)
            .mkString(",") + "/_count"

      val builder = CountBodyBuilderFn(request)
      val body    = builder.string()

      ElasticRequest("GET", endpoint, HttpEntity(body, "application/json"))
    }
  }
}

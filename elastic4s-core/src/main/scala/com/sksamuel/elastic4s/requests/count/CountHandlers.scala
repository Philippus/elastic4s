package com.sksamuel.elastic4s.requests.count

import com.sksamuel.elastic4s.{ElasticRequest, ElasticUrlEncoder, Handler, HttpEntity}

case class CountResponse(count: Long)

trait CountHandlers {

  implicit object CountHandler extends Handler[CountRequest, CountResponse] {

    override def build(request: CountRequest): ElasticRequest = {

      val endpoint = if(request.indexes.isEmpty)
          "/_all/_count"
        else
          "/" + request.indexes.values.map(ElasticUrlEncoder.encodeUrlFragment).mkString(",") + "/_count"

      val builder = CountBodyBuilderFn(request)
      val body    = builder.string()

      ElasticRequest("GET", endpoint, HttpEntity(body, "application/json"))
    }
  }
}

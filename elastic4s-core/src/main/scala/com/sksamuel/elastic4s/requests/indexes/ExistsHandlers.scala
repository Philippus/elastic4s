package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.requests.ExistsRequest
import com.sksamuel.elastic4s._

trait ExistsHandlers {

  implicit object ExistsHandler extends Handler[ExistsRequest, Boolean] {

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse): Either[ElasticError, Boolean] = Right(response.statusCode == 200)
    }

    override def build(request: ExistsRequest): ElasticRequest = {
      val endpoint = "/" + request.index.name + "/_doc/" + request.id
      val method   = "HEAD"
      ElasticRequest(method, endpoint)
    }
  }
}

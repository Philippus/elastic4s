package com.sksamuel.elastic4s.handlers.exists

import com.sksamuel.elastic4s.requests.exists.ExistsRequest
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpResponse, ResponseHandler}

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

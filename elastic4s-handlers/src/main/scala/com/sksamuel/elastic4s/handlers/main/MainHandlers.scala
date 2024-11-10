package com.sksamuel.elastic4s.handlers.main

import com.sksamuel.elastic4s.requests.main._
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpResponse, ResponseHandler}

trait MainHandlers {
  implicit object MainHandlers extends Handler[MainRequest, MainResponse] {

    override def responseHandler: ResponseHandler[MainResponse] = new ResponseHandler[MainResponse] {
      override def handle(response: HttpResponse): Right[Nothing, MainResponse] =
        response.statusCode match {
          case 200 => Right(ResponseHandler.fromResponse[MainResponse](response))
          case _ => sys.error("Invalid response")
        }
    }

    override def build(request: MainRequest): ElasticRequest = {
      val endpoint = "/"

      ElasticRequest("GET", endpoint)
    }
  }
}

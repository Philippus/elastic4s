package com.sksamuel.elastic4s.handlers.reloadsearchanalyzers

import com.sksamuel.elastic4s.requests.reloadsearchanalyzers._
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpResponse, ResponseHandler}

trait ReloadSearchAnalyzersHandlers {
  implicit object ReloadSearchAnalyzersHandler extends Handler[ReloadSearchAnalyzersRequest, ReloadSearchAnalyzersResponse] {

    override def responseHandler: ResponseHandler[ReloadSearchAnalyzersResponse] = new ResponseHandler[ReloadSearchAnalyzersResponse] {
      override def handle(response: HttpResponse): Right[Nothing, ReloadSearchAnalyzersResponse] =
        response.statusCode match {
          case 404 | 200 => Right(ResponseHandler.fromResponse[ReloadSearchAnalyzersResponse](response))
          case _ => sys.error("Invalid response")
        }
    }

    override def build(request: ReloadSearchAnalyzersRequest): ElasticRequest = {
      val endpoint = s"/${request.indexes.string(true)}/_reload_search_analyzers"

      ElasticRequest("POST", endpoint)
    }
  }
}

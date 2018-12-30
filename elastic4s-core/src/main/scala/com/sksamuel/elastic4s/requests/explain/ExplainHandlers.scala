package com.sksamuel.elastic4s.requests.explain

import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity, HttpResponse, ResponseHandler}

trait ExplainHandlers {

  implicit object ExplainHandler extends Handler[ExplainRequest, ExplainResponse] {

    override def responseHandler: ResponseHandler[ExplainResponse] = new ResponseHandler[ExplainResponse] {
      override def handle(response: HttpResponse): Right[Nothing, ExplainResponse] =
        response.statusCode match {
          case 404 | 200 => Right(ResponseHandler.fromResponse[ExplainResponse](response))
          case _         => sys.error("Invalid response")
        }
    }

    override def build(request: ExplainRequest): ElasticRequest = {

      val endpoint = s"/${request.indexAndType.index}/${request.indexAndType.`type`}/${request.id}/_explain"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.routing.map(_.toString).foreach(params.put("routing", _))
      request.parent.map(_.toString).foreach(params.put("parent", _))
      request.preference.map(_.toString).foreach(params.put("preference", _))
      request.lenient.map(_.toString).foreach(params.put("lenient", _))

      val body   = ExplainBodyFn(request).string()
      val entity = HttpEntity(body, "application/json")

      ElasticRequest("GET", endpoint, params.toMap, entity)
    }
  }
}

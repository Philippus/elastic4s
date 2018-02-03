package com.sksamuel.elastic4s.http.explain

import com.sksamuel.elastic4s.explain.ExplainRequest
import com.sksamuel.elastic4s.http._
import org.apache.http.entity.ContentType

trait ExplainHandlers {

  implicit object ExplainHandler extends Handler[ExplainRequest, ExplainResponse] {

    override def responseHandler: ResponseHandler[ExplainResponse] = new ResponseHandler[ExplainResponse] {
      override def handle(response: HttpResponse) =
        response.statusCode match {
          case 404 | 200 => Right(ResponseHandler.fromResponse[ExplainResponse](response))
          case _         => sys.error("Invalid response")
        }
    }

    override def requestHandler(request: ExplainRequest): ElasticRequest = {

      val endpoint = s"/${request.indexAndType.index}/${request.indexAndType.`type`}/${request.id}/_explain"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.routing.map(_.toString).foreach(params.put("routing", _))
      request.parent.map(_.toString).foreach(params.put("parent", _))
      request.preference.map(_.toString).foreach(params.put("preference", _))
      request.lenient.map(_.toString).foreach(params.put("lenient", _))

      val body   = ExplainBodyFn(request).string()
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)

      ElasticRequest("GET", endpoint, params.toMap, entity)
    }
  }
}

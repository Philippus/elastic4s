package com.sksamuel.elastic4s.http.explain

import com.sksamuel.elastic4s.explain.ExplainDefinition
import com.sksamuel.elastic4s.http.{HttpEntity, HttpExecutable, HttpRequestClient, HttpResponse, ResponseHandler}
import org.apache.http.entity.ContentType

import scala.concurrent.Future

trait ExplainImplicits {

  implicit object ExplainHttpExec extends HttpExecutable[ExplainDefinition, ExplainResponse] {

    override def responseHandler: ResponseHandler[ExplainResponse] = ResponseHandler.failure404

    override def execute(client: HttpRequestClient,
                         request: ExplainDefinition): Future[HttpResponse] = {

      val endpoint = s"/${request.indexAndType.index}/${request.indexAndType.`type`}/${request.id}/_explain"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.routing.map(_.toString).foreach(params.put("routing", _))
      request.parent.map(_.toString).foreach(params.put("parent", _))
      request.preference.map(_.toString).foreach(params.put("preference", _))
      request.lenient.map(_.toString).foreach(params.put("lenient", _))

      val body = ExplainBodyFn(request).string()
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)

      client.async("GET", endpoint, params.toMap, entity)
    }
  }
}

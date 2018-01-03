package com.sksamuel.elastic4s.http.explain

import cats.Functor
import com.sksamuel.elastic4s.explain.ExplainDefinition
import com.sksamuel.elastic4s.http._
import org.apache.http.entity.ContentType

trait ExplainImplicits {

  implicit object ExplainHttpExec extends HttpExecutable[ExplainDefinition, ExplainResponse] {

    override def responseHandler: ResponseHandler[ExplainResponse] = new ResponseHandler[ExplainResponse] {
      override def handle(response: HttpResponse) = {
        response.statusCode match {
          case 404 | 200 => Right(ResponseHandler.fromResponse[ExplainResponse](response))
          case _ => sys.error("Invalid response")
        }
      }
    }

    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient,
                         request: ExplainDefinition): F[HttpResponse] = {

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

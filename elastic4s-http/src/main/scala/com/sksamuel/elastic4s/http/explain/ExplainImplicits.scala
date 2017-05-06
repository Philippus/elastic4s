package com.sksamuel.elastic4s.http.explain

import com.sksamuel.elastic4s.explain.ExplainDefinition
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._
import scala.concurrent.Future

trait ExplainImplicits {

  implicit object ExplainHttpExec extends HttpExecutable[ExplainDefinition, ExplainResponse] {
    override def execute(client: RestClient,
                         request: ExplainDefinition): Future[ExplainResponse] = {

      val method = "GET"
      val endpoint = s"/${request.indexAndType.index}/${request.indexAndType.`type`}/${request.id}/_explain"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.routing.map(_.toString).foreach(params.put("routing", _))
      request.parent.map(_.toString).foreach(params.put("parent", _))
      request.preference.map(_.toString).foreach(params.put("preference", _))
      request.lenient.map(_.toString).foreach(params.put("lenient", _))

      val body = ExplainBodyFn(request).string()
      logger.debug(s"Executing validate query $body")
      val entity = new StringEntity(body)

      val fn = client.performRequestAsync(method, endpoint, params.asJava, entity, _: ResponseListener)
      client.async(method, endpoint, params.toMap, entity, ResponseHandler.failure404)
    }
  }
}

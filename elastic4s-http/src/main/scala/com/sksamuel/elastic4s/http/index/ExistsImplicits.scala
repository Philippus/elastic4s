package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.ExistsDefinition
import com.sksamuel.elastic4s.http.{ElasticError, HttpExecutable, HttpClient, HttpResponse, ResponseHandler}

import scala.concurrent.Future

trait ExistsImplicits {

  implicit object ExistsHttpExecutable extends HttpExecutable[ExistsDefinition, Boolean] {

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse): Either[ElasticError, Boolean] = Right(response.statusCode == 200)
    }

    override def execute(client: HttpClient, request: ExistsDefinition): Future[HttpResponse] = {
      val endpoint = "/" + request.index.name + "/" + request.`type` + "/" + request.id
      val method   = "HEAD"
      client.async(method, endpoint, Map.empty)
    }
  }
}

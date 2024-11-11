package com.sksamuel.elastic4s.handlers.indexlifecyclemanagement

import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement._

trait IndexLifecycleManagementHandlers {
  implicit object IndexLifecycleStatusHandler extends Handler[GetIlmStatusRequest, GetIlmStatusResponse] {
    override def responseHandler: ResponseHandler[GetIlmStatusResponse] = new ResponseHandler[GetIlmStatusResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, GetIlmStatusResponse] = {
        response.statusCode match {
          case 200 | 201 => Right(ResponseHandler.fromResponse[GetIlmStatusResponse](response))
          case 400 => Left(ElasticErrorParser.parse(response))
          case _ => sys.error("Invalid response")
        }
      }
    }

    override def build(request: GetIlmStatusRequest): ElasticRequest = {
      val endpoint = "/_ilm/status"

      ElasticRequest("GET", endpoint)
    }
  }

  implicit object IndexLifecycleStartHandler extends Handler[StartIlmRequest, StartIlmResponse] {
    override def responseHandler: ResponseHandler[StartIlmResponse] = new ResponseHandler[StartIlmResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, StartIlmResponse] = {
        response.statusCode match {
          case 200 | 201 => Right(ResponseHandler.fromResponse[StartIlmResponse](response))
          case 400 => Left(ElasticErrorParser.parse(response))
          case _ => sys.error("Invalid response")
        }
      }
    }

    override def build(request: StartIlmRequest): ElasticRequest = {
      val endpoint = "/_ilm/start"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.masterTimeout.foreach(params.put("master_timeout", _))
      request.timeout.foreach(params.put("timeout", _))

      ElasticRequest("POST", endpoint, params.toMap)
    }
  }

  implicit object IndexLifecycleStopHandler extends Handler[StopIlmRequest, StopIlmResponse] {
    override def responseHandler: ResponseHandler[StopIlmResponse] = new ResponseHandler[StopIlmResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, StopIlmResponse] = {
        response.statusCode match {
          case 200 | 201 => Right(ResponseHandler.fromResponse[StopIlmResponse](response))
          case 400 => Left(ElasticErrorParser.parse(response))
          case _ => sys.error("Invalid response")
        }
      }
    }

    override def build(request: StopIlmRequest): ElasticRequest = {
      val endpoint = "/_ilm/stop"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.masterTimeout.foreach(params.put("master_timeout", _))
      request.timeout.foreach(params.put("timeout", _))

      ElasticRequest("POST", endpoint, params.toMap)
    }
  }
}

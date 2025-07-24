package com.sksamuel.elastic4s.handlers.indexlifecyclemanagement

import com.sksamuel.elastic4s.HttpEntity.ByteArrayEntity
import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement._

import java.nio.charset.StandardCharsets
import scala.util.Try

trait IndexLifecycleManagmentHandlers {
  implicit object IndexLifecycleStatusHandler extends Handler[GetIlmStatusRequest, GetIlmStatusResponse] {
    override def responseHandler: ResponseHandler[GetIlmStatusResponse] = (response: HttpResponse) => {
      response.statusCode match {
        case 200 | 201 => Right(ResponseHandler.fromResponse[GetIlmStatusResponse](response))
        case 400       => Left(ElasticErrorParser.parse(response))
        case _         => sys.error("Invalid response")
      }
    }

    override def build(request: GetIlmStatusRequest): ElasticRequest = {
      val endpoint = "/_ilm/status"

      ElasticRequest("GET", endpoint)
    }
  }

  implicit object IndexLifecycleStartHandler extends Handler[StartIlmRequest, StartIlmResponse] {
    override def responseHandler: ResponseHandler[StartIlmResponse] = (response: HttpResponse) => {
      response.statusCode match {
        case 200 | 201 => Right(ResponseHandler.fromResponse[StartIlmResponse](response))
        case 400       => Left(ElasticErrorParser.parse(response))
        case _         => sys.error("Invalid response")
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
    override def responseHandler: ResponseHandler[StopIlmResponse] = (response: HttpResponse) => {
      response.statusCode match {
        case 200 | 201 => Right(ResponseHandler.fromResponse[StopIlmResponse](response))
        case 400       => Left(ElasticErrorParser.parse(response))
        case _         => sys.error("Invalid response")
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

  private val policyPath = (policyName: String) => s"/_ilm/policy/$policyName"

  implicit object CreateElasticPolicyHandler
      extends Handler[CreateLifecyclePolicyRequest, CreateLifecyclePolicyResponse] {

    override def build(request: CreateLifecyclePolicyRequest): ElasticRequest = {
      val endpoint = policyPath(request.policy.name)
      val params   = List(
        request.masterTimeout.map("master_timeout" -> _),
        request.timeout.map("timeout" -> _)
      ).flatten.toMap

      val body   = IndexLifecyclePolicyContentBuilder(request.policy).string
      val entity = ByteArrayEntity(body.getBytes(StandardCharsets.UTF_8), Some("application/json"))

      ElasticRequest("PUT", endpoint, params, entity)
    }
  }

  implicit object GetElasticPolicyHandler
      extends Handler[GetIndexLifecyclePolicyRequest, Option[GetIndexLifecyclePolicyResponse]] {

    override def build(request: GetIndexLifecyclePolicyRequest): ElasticRequest = {
      val endpoint = policyPath(request.policyName)
      val params   = List(
        request.masterTimeout.map("master_timeout" -> _),
        request.timeout.map("timeout" -> _)
      ).flatten.toMap

      ElasticRequest("GET", endpoint, params)
    }

    override def responseHandler: ResponseHandler[Option[GetIndexLifecyclePolicyResponse]] =
      (response: HttpResponse) => {
        response.statusCode match {
          case 200 | 201 =>
            response.entity.map(_.content).map { responseString =>
              Try(JacksonSupport.mapper.readTree(responseString))
                .flatMap { node => Try(GetIndexLifecyclePolicyResponse.deserialize(node)) }
                .fold(
                  err => Left(ElasticError.fromThrowable(err)),
                  res => Right(Some(res))
                )
            }.getOrElse(Right(None))
          case 404       =>
            Right(None)
          case _         =>
            Left(ElasticErrorParser.parse(response))
        }
      }
  }

  implicit object DeleteIndexLifecyclePolicyHandler
      extends Handler[DeleteIndexLifecyclePolicyRequest, DeleteIndexLifecyclePolicyResponse] {

    override def build(request: DeleteIndexLifecyclePolicyRequest): ElasticRequest = {
      val endpoint = policyPath(request.policyName)
      val params   = List(
        request.masterTimeout.map("master_timeout" -> _),
        request.timeout.map("timeout" -> _)
      ).flatten.toMap

      ElasticRequest("DELETE", endpoint, params)
    }

    override def responseHandler: ResponseHandler[DeleteIndexLifecyclePolicyResponse] = { (response: HttpResponse) =>
      super.responseHandler.handle(response) match {
        case Left(error: ElasticError) if error.`type` == "resource_not_found_exception" =>
          Right(DeleteIndexLifecyclePolicyResponse(false))
        case Left(error)                                                                 => Left(error)
        case Right(response)                                                             => Right(response)
      }

    }
  }
}

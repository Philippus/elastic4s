package com.sksamuel.elastic4s.requests.indexes

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.RefreshPolicyHttpValue
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, ResponseHandler}
import com.sksamuel.exts.collection.Maps

trait IndexHandlers {

  implicit object IndexHandler extends Handler[IndexRequest, IndexResponse] {

    override def responseHandler: ResponseHandler[IndexResponse] = new ResponseHandler[IndexResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, IndexResponse] = response.statusCode match {
        case 201 | 200       => Right(ResponseHandler.fromResponse[IndexResponse](response))
        case 400 | 409 | 500 => Left(ElasticError.parse(response))
        case _               => sys.error(response.toString)
      }
    }

    override def build(request: IndexRequest): ElasticRequest = {

      val (method, endpoint) = request.id match {
        case Some(id) =>
          "PUT" -> s"/${URLEncoder.encode(request.indexAndType.index, StandardCharsets.UTF_8.name())}/${URLEncoder
            .encode(request.indexAndType.`type`, StandardCharsets.UTF_8.name())}/${URLEncoder.encode(id.toString, StandardCharsets.UTF_8.name())}"
        case None =>
          "POST" -> s"/${URLEncoder.encode(request.indexAndType.index, StandardCharsets.UTF_8.name())}/${URLEncoder
            .encode(request.indexAndType.`type`, StandardCharsets.UTF_8.name())}"
      }

      val params = scala.collection.mutable.Map.empty[String, String]
      request.createOnly.foreach(
        createOnly =>
          if (createOnly)
            params.put("op_type", "create")
      )
      request.routing.foreach(params.put("routing", _))
      request.parent.foreach(params.put("parent", _))
      request.timeout.foreach(params.put("timeout", _))
      request.pipeline.foreach(params.put("pipeline", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.map(VersionTypeHttpString.apply).foreach(params.put("version_type", _))

      val body   = IndexContentBuilder(request)
      val entity = HttpEntity(body.string, "application/json")

      logger.debug(s"Endpoint=$endpoint")
      ElasticRequest(method, endpoint, params.toMap, entity)
    }
  }

  implicit object GetIndexHandler extends Handler[GetIndexRequest, Map[String, GetIndexResponse]] {

    override def build(request: GetIndexRequest): ElasticRequest = {
      val endpoint = "/" + request.index
      val method   = "GET"
      ElasticRequest(method, endpoint)
    }
  }
}

case class Mapping(properties: Map[String, Field])

case class Field(`type`: String)

case class GetIndexResponse(aliases: Map[String, Map[String, Any]],
                            mappings: Map[String, Mapping],
                            @JsonProperty("settings") private val _settings: Map[String, Any]) {
  def settings: Map[String, Any] = Maps.flatten(_settings, ".")
}

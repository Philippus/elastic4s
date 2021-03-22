package com.sksamuel.elastic4s.requests.indexes

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.HttpEntity.ByteArrayEntity
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.requests.common.RefreshPolicyHttpValue
import com.sksamuel.exts.collection.Maps

trait IndexHandlers {

  implicit object IndexHandler extends Handler[IndexRequest, IndexResponse] {

    override def responseHandler: ResponseHandler[IndexResponse] = new ResponseHandler[IndexResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, IndexResponse] = response.statusCode match {
        case 201 | 200                   => Right(ResponseHandler.fromResponse[IndexResponse](response))
        case 400 | 401 | 403 | 409 | 500 => Left(ElasticError.parse(response))
        case _                           => sys.error(response.toString)
      }
    }

    override def build(request: IndexRequest): ElasticRequest = {

      val (method, endpoint) = request.id match {
        case Some(id) =>
          "PUT" -> s"/${ElasticUrlEncoder.encodeUrlFragment(request.index.name)}/_doc/${ElasticUrlEncoder.encodeUrlFragment(id)}"
        case None =>
          "POST" -> s"/${ElasticUrlEncoder.encodeUrlFragment(request.index.name)}/_doc"
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
      request.ifPrimaryTerm.map(_.toString).foreach(params.put("if_primary_term", _))
      request.ifSeqNo.map(_.toString).foreach(params.put("if_seq_no", _))
      request.versionType.map(VersionTypeHttpString.apply).foreach(params.put("version_type", _))

      val body   = IndexContentBuilder(request)
      val entity = ByteArrayEntity(body.getBytes, Some("application/json"))

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

    override def responseHandler: ResponseHandler[Map[String, GetIndexResponse]] = {
      ResponseHandler.default[Map[String, GetIndexResponse]].map { map =>
        map.mapValues { resp => if (resp.mappings.meta == null) resp.copy(mappings = resp.mappings.copy(meta = Map.empty)) else resp }.toMap
      }
    }
  }
}

case class Mapping(properties: Map[String, Field],
                   @JsonProperty("_meta") meta: Map[String, String] = Map.empty)

case class Field(`type`: String)

case class GetIndexResponse(aliases: Map[String, Map[String, Any]],
                            mappings: Mapping,
                            @JsonProperty("settings") private val _settings: Map[String, Any]) {
  def settings: Map[String, Any] = Maps.flatten(_settings, ".")
}

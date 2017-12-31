package com.sksamuel.elastic4s.http.index

import java.net.URLEncoder

import cats.Functor
import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.indexes.{GetIndex, IndexContentBuilder, IndexDefinition}
import com.sksamuel.exts.collection.Maps
import org.apache.http.entity.ContentType

trait IndexImplicits extends IndexShowImplicits {

  implicit object IndexHttpExecutable extends HttpExecutable[IndexDefinition, IndexResponse] {

    override def responseHandler: ResponseHandler[IndexResponse] = new ResponseHandler[IndexResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, IndexResponse] = response.statusCode match {
        case 201 | 200 => Right(ResponseHandler.fromResponse[IndexResponse](response))
        case 400 | 409 | 500 => Left(ElasticError.parse(response))
        case _ => sys.error(response.toString)
      }
    }

    override def execute[F[_]: FromListener](client: HttpRequestClient, request: IndexDefinition): F[HttpResponse] = {

      val (method, endpoint) = request.id match {
        case Some(id) => "PUT" -> s"/${URLEncoder.encode(request.indexAndType.index)}/${URLEncoder.encode(request.indexAndType.`type`)}/${URLEncoder.encode(id.toString)}"
        case None => "POST" -> s"/${URLEncoder.encode(request.indexAndType.index)}/${URLEncoder.encode(request.indexAndType.`type`)}"
      }

      val params = scala.collection.mutable.Map.empty[String, String]
      request.createOnly.foreach(createOnly =>
        if(createOnly) {
          params.put("op_type", "create")
        }
      )
      request.routing.foreach(params.put("routing", _))
      request.parent.foreach(params.put("parent", _))
      request.timeout.foreach(params.put("timeout", _))
      request.pipeline.foreach(params.put("pipeline", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.map(VersionTypeHttpString.apply).foreach(params.put("version_type", _))

      val body = IndexContentBuilder(request)
      val entity = HttpEntity(body.string, ContentType.APPLICATION_JSON.getMimeType)

      logger.debug(s"Endpoint=$endpoint")
      client.async(method, endpoint, params.toMap, entity)
    }
  }

  implicit object GetIndexHttpExecutable extends HttpExecutable[GetIndex, Map[String, GetIndexResponse]] {

    override def execute[F[_]: FromListener](client: HttpRequestClient, request: GetIndex): F[HttpResponse] = {
      val endpoint = "/" + request.index
      val method = "GET"
      client.async(method, endpoint, Map.empty)
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

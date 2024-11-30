package com.sksamuel.elastic4s.handlers.get

import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.elastic4s.handlers.common.FetchSourceContextQueryParameterFn
import com.sksamuel.elastic4s.handlers.{ElasticErrorParser, VersionTypeHttpString}
import com.sksamuel.elastic4s.requests.get.{GetRequest, GetResponse, MultiGetRequest, MultiGetResponse}
import com.sksamuel.elastic4s.{
  ElasticError,
  ElasticRequest,
  ElasticUrlEncoder,
  Handler,
  HttpEntity,
  HttpResponse,
  ResponseHandler
}

trait GetHandlers {

  implicit object MultiGetHandler extends Handler[MultiGetRequest, MultiGetResponse] {

    override def responseHandler: ResponseHandler[MultiGetResponse] = new ResponseHandler[MultiGetResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, MultiGetResponse] = response.statusCode match {
        case 404 | 500 => sys.error(response.toString)
        case _         => Right(ResponseHandler.fromResponse[MultiGetResponse](response))
      }
    }

    override def build(request: MultiGetRequest): ElasticRequest = {
      val body   = MultiGetBodyBuilder(request).string
      val entity = HttpEntity(body, "application/json")

      val params = scala.collection.mutable.Map.empty[String, String]
      request.preference.foreach(params.put("preference", _))
      request.refresh.map(_.toString).foreach(params.put("refresh", _))
      request.realtime.map(_.toString).foreach(params.put("realtime", _))

      ElasticRequest("GET", "/_mget", params.toMap, entity)
    }
  }

  implicit object GetHandler extends Handler[GetRequest, GetResponse] {

    override def responseHandler: ResponseHandler[GetResponse] = new ResponseHandler[GetResponse] {

      override def handle(response: HttpResponse): Either[ElasticError, GetResponse] = {

        def bad(status: Int): Left[ElasticError, GetResponse] = {
          val node = ResponseHandler.fromResponse[JsonNode](response)
          if (node.has("error") && node.get("error").isObject)
            Left(ElasticErrorParser.parse(response))
          else
            Left(ElasticError(response.entity.get.content, response.entity.get.content, None, None, None, Nil, None))
        }

        def good = Right(ResponseHandler.fromResponse[GetResponse](response))

        response.statusCode match {
          case 200   => good
          // 404s are odd, can be different document types
          case 404   =>
            val node = ResponseHandler.fromResponse[JsonNode](response)
            if (node.has("error")) bad(404) else good
          case other => bad(other)
        }
      }
    }

    override def build(request: GetRequest): ElasticRequest = {

      val endpoint =
        s"/${ElasticUrlEncoder.encodeUrlFragment(request.index.index)}/_doc/${ElasticUrlEncoder.encodeUrlFragment(request.id)}"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.fetchSource.foreach { context =>
        FetchSourceContextQueryParameterFn(context).foreach { case (key, value) => params.put(key, value) }
      }
      if (request.storedFields.nonEmpty)
        params.put("stored_fields", request.storedFields.mkString(","))
      request.parent.foreach(params.put("parent", _))
      request.routing.foreach(params.put("routing", _))
      request.preference.foreach(params.put("preference", _))
      request.refresh.map(_.toString).foreach(params.put("refresh", _))
      request.realtime.map(_.toString).foreach(params.put("realtime", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.map(VersionTypeHttpString.apply).foreach(params.put("version_type", _))

      ElasticRequest("GET", endpoint, params.toMap)
    }
  }
}

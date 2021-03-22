package com.sksamuel.elastic4s.requests.get

import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.elastic4s.requests.common.FetchSourceContextQueryParameterFn
import com.sksamuel.elastic4s.requests.indexes.VersionTypeHttpString
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, ElasticUrlEncoder, Handler, HitReader, HttpEntity, HttpResponse, ResponseHandler}
import com.sksamuel.exts.Logging

import scala.util.Try

case class MultiGetResponse(docs: Seq[GetResponse]) {
  def items: Seq[GetResponse] = docs
  def size: Int               = docs.size

  def to[T: HitReader]: IndexedSeq[T]                        = docs.map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Try[T]] = docs.map(_.safeTo[T]).toIndexedSeq
}

trait GetHandlers {

  implicit object MultiGetHandler extends Handler[MultiGetRequest, MultiGetResponse] with Logging {

    override def responseHandler: ResponseHandler[MultiGetResponse] = new ResponseHandler[MultiGetResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, MultiGetResponse] = response.statusCode match {
        case 404 | 500 => sys.error(response.toString)
        case _ => Right(ResponseHandler.fromResponse[MultiGetResponse](response))
      }
    }

    override def build(request: MultiGetRequest): ElasticRequest = {
      val body   = MultiGetBodyBuilder(request).string()
      val entity = HttpEntity(body, "application/json")

      val params = scala.collection.mutable.Map.empty[String, String]
      request.preference.foreach(params.put("preference", _))
      request.refresh.map(_.toString).foreach(params.put("refresh", _))
      request.realtime.map(_.toString).foreach(params.put("realtime", _))

      ElasticRequest("GET", "/_mget", params.toMap, entity)
    }
  }

  implicit object GetHandler extends Handler[GetRequest, GetResponse] with Logging {

    override def responseHandler: ResponseHandler[GetResponse] = new ResponseHandler[GetResponse] {

      override def handle(response: HttpResponse): Either[ElasticError, GetResponse] = {

        def bad(status: Int): Left[ElasticError, GetResponse] = {
          val node = ResponseHandler.fromResponse[JsonNode](response)
          if (node.has("error") && node.get("error").isObject)
            Left(ElasticError.parse(response))
          else
            Left(ElasticError(response.entity.get.content, response.entity.get.content, None, None, None, Nil, None))
        }

        def good = Right(ResponseHandler.fromResponse[GetResponse](response))

        response.statusCode match {
          case 200 => good
          // 404s are odd, can be different document types
          case 404 =>
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

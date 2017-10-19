package com.sksamuel.elastic4s.http.count

import java.net.URLEncoder

import com.sksamuel.elastic4s.count.CountDefinition
import com.sksamuel.elastic4s.http.update.RequestFailure
import com.sksamuel.elastic4s.http.{HttpEntity, HttpExecutable, HttpRequestClient, HttpResponse, ResponseHandler}
import com.sksamuel.exts.OptionImplicits._
import org.apache.http.entity.ContentType

import scala.concurrent.Future

case class CountResponse(count: Long)

trait CountImplicits {

  implicit object CountHttpExecutable extends HttpExecutable[CountDefinition, Either[RequestFailure, CountResponse]] {

    override def responseHandler = new ResponseHandler[Either[RequestFailure, CountResponse]] {
      override def doit(response: HttpResponse): Either[RequestFailure, CountResponse] = response.statusCode match {
        case 200 =>
          val entity = response.entity.getOrError("No entity defined")
          Right(ResponseHandler.fromEntity[CountResponse](entity))
        case _ => Left(ResponseHandler.fromEntity[RequestFailure](response.entity.get))
      }
    }

    override def execute(client: HttpRequestClient, request: CountDefinition): Future[HttpResponse] = {

      val endpoint = if (request.indexes.isEmpty && request.types.isEmpty)
        "/_count"
      else if (request.indexes.isEmpty)
        "/_all/" + request.types.map(URLEncoder.encode).mkString(",") + "/_count"
      else if (request.types.isEmpty)
        "/" + request.indexes.values.map(URLEncoder.encode).mkString(",") + "/_count"
      else
        "/" + request.indexes.values.map(URLEncoder.encode).mkString(",") + "/" + request.types.map(URLEncoder.encode).mkString(",") + "/_count"

      val builder = CountBodyBuilderFn(request)
      val body = builder.string()

      client.async("GET", endpoint, Map.empty, HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType))
    }
  }
}

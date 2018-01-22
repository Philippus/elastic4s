package com.sksamuel.elastic4s.http

import com.sksamuel.exts.Logging
import org.apache.http.entity.ContentType

import scala.concurrent.Future

/**
  * @tparam T the type of the request object handled by this handler
  * @tparam U the type of the response object returned by this handler
  */
abstract class HttpExecutable[T, U: Manifest] extends Logging {

  def responseHandler: ResponseHandler[U] = ResponseHandler.default[U]

  /**
    * Executes a request in a client.
    * Returns a Future of a Http Response.
    *
    * Basically the execute method of a HttpExecutable knows how to create the appropriate
    * http request for a request object - ie, query params, post vs get, etc.
    *
    * The return Http response will then be passed to the response handler for conversion
    * into a request specific return type.
    *
    * @param client
    * @param request
    * @return
    */
  def execute(client: HttpRequestClient, request: T): Future[HttpResponse]
}

class MarshallableHttpExecutable[T: RequestMarshaller, U: Manifest] extends HttpExecutable[T, U] {
  def execute(client: HttpRequestClient, request: T): Future[HttpResponse] = {
    val marshalled: MarshalledRequest = implicitly[RequestMarshaller[T]].marshal(request)

    marshalled.body match {
      case Some(body) =>
        client.async(marshalled.method, marshalled.endpoint, marshalled.params, HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType))
      case None =>
        client.async(marshalled.method, marshalled.endpoint, marshalled.params)
    }
  }
}

trait RequestMarshaller[T] {
  def marshal(t: T): MarshalledRequest
}

case class MarshalledRequest(method: String, endpoint: String, params: Map[String, String], body: Option[String])

package com.sksamuel.elastic4s.http

import com.sksamuel.exts.Logging

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
  def execute[F[_]: AsyncExecutor](client: HttpRequestClient, request: T): F[HttpResponse]
}

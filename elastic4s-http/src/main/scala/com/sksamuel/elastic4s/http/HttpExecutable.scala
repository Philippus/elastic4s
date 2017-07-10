package com.sksamuel.elastic4s.http

import com.sksamuel.exts.Logging

import scala.concurrent.Future

/**
  * @tparam T the type of the request object handled by this handler
  * @tparam U the type of the response object returned by this handler
  */
abstract class HttpExecutable[T, U: Manifest] extends Logging {

  def responseHandler: ResponseHandler[U] = ResponseHandler.default[U]

  // executes the request in the given client, returning the raw http response
  def execute(client: HttpRequestClient, request: T): Future[HttpResponse]
}

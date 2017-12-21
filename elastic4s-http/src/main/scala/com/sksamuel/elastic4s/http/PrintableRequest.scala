package com.sksamuel.elastic4s.http

import com.sksamuel.exts.Logging

/**
  * @tparam T the type of the request object to be printed
  */
abstract class PrintableRequest[T] extends Logging {

  def endpoint(request: T): String

  def body(request: T): Option[String] = None

  /**
   * Print the endpoint and JSON body (if applicable) of the given request.
   */
  def print(request: T): String = (endpoint(request) +: body(request).toSeq).mkString("\n")
}

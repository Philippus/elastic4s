package com.sksamuel.elastic4s

import com.fasterxml.jackson.module.scala.JavaTypeable
import org.slf4j.{Logger, LoggerFactory}

/** A [[Handler]] is a typeclass used to create [[ElasticRequest]] instances from elastic4s models, which are the sent
  * to the elasticsearch server, as well as returning a [[ResponseHandler]] which handles the response from the server.
  *
  * @tparam T
  *   the type of the request object handled by this handler
  * @tparam U
  *   the type of the response object returned by this handler
  */
abstract class Handler[T, U: JavaTypeable] {

  protected val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  protected val TaskRegex = """\{"task":"(.*):(.*)"\}""".r

  def responseHandler: ResponseHandler[U] = ResponseHandler.default[U]
  def build(t: T): ElasticRequest
}

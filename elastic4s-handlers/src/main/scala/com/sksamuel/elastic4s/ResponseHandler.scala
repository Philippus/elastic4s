package com.sksamuel.elastic4s

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.`type`.TypeFactory
import com.fasterxml.jackson.module.scala.JavaTypeable
import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s.ext.OptionImplicits.RichOption
import org.slf4j.{Logger, LoggerFactory}

trait ResponseHandler[U] {
  self =>

  /** Accepts a HttpResponse and returns an Either of an ElasticError or a type specific to the request as determined by
    * the instance of this handler.
    */
  def handle(response: HttpResponse): Either[ElasticError, U]

  def map[V](fn: U => V): ResponseHandler[V] = new ResponseHandler[V] {
    override def handle(response: HttpResponse): Either[ElasticError, V] = self.handle(response).map(fn)
  }
}

// a ResponseHandler that marshalls the body into the required type using Jackson
// the response body is converted into a string using a codec derived from the content encoding header
// if the content encoding header is null, then UTF-8 is assumed
object ResponseHandler {

  protected val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  def json(entity: HttpEntity.StringEntity): JsonNode = fromEntity[JsonNode](entity)

  def fromNode[U: JavaTypeable](node: JsonNode): U = {
    logger.debug(
      s"Attempting to unmarshall json node to ${implicitly[JavaTypeable[U]].asJavaType(TypeFactory.defaultInstance).getRawClass.getName}"
    )
    JacksonSupport.mapper.readValue[U](JacksonSupport.mapper.writeValueAsBytes(node))
  }

  def fromResponse[U: JavaTypeable](response: HttpResponse): U =
    fromEntity(response.entity.getOrError("No entity defined but was expected"))

  def fromEntity[U: JavaTypeable](entity: HttpEntity.StringEntity): U = {
    logger.debug(
      s"Attempting to unmarshall response to ${implicitly[JavaTypeable[U]].asJavaType(TypeFactory.defaultInstance).getRawClass.getName}"
    )
    logger.debug(entity.content)
    JacksonSupport.mapper.readValue[U](entity.content)
  }

  def default[U: JavaTypeable]    = new DefaultResponseHandler[U]
  def failure404[U: JavaTypeable] = new NotFound404ResponseHandler[U]
}

// standard response handler, 200-204s are ok, and everything else is marhalled into an error
class DefaultResponseHandler[U: JavaTypeable] extends ResponseHandler[U] {
  self =>

  override def handle(response: HttpResponse): Either[ElasticError, U] = response.statusCode match {
    case 200 | 201 | 202 | 203 | 204 =>
      val entity = response.entity.getOrError("No entity defined")
      Right(ResponseHandler.fromEntity[U](entity))
    case _                           =>
      Left(ElasticErrorParser.parse(response))
  }
}

class NotFound404ResponseHandler[U: JavaTypeable] extends DefaultResponseHandler[U] {
  override def handle(response: HttpResponse): Either[ElasticError, U] =
    response.statusCode match {
      case 404 | 500 => sys.error(response.toString)
      case _         => super.handle(response)
    }
}

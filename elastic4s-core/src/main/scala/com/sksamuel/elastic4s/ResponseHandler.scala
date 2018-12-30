package com.sksamuel.elastic4s

import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.exts.Logging
import com.sksamuel.exts.OptionImplicits._

trait ResponseHandler[U] {

  /**
    * Accepts a HttpResponse and returns an Either of an ElasticError or a type specific to the request
    * as determined by the instance of this handler.
    */
  def handle(response: HttpResponse): Either[ElasticError, U]
}

// a ResponseHandler that marshalls the body into the required type using Jackson
// the response body is converted into a string using a codec derived from the content encoding header
// if the content encoding header is null, then UTF-8 is assumed
object ResponseHandler extends Logging {

  def json(entity: HttpEntity.StringEntity): JsonNode = fromEntity[JsonNode](entity)

  def fromNode[U: Manifest](node: JsonNode): U = {
    logger.debug(s"Attempting to unmarshall json node to ${manifest.runtimeClass.getName}")
    JacksonSupport.mapper.readValue[U](JacksonSupport.mapper.writeValueAsBytes(node))
  }

  def fromResponse[U: Manifest](response: HttpResponse): U =
    fromEntity(response.entity.getOrError("No entity defined but was expected"))

  def fromEntity[U: Manifest](entity: HttpEntity.StringEntity): U = {
    logger.debug(s"Attempting to unmarshall response to ${manifest.runtimeClass.getName}")
    logger.debug(entity.content)
    JacksonSupport.mapper.readValue[U](entity.content)
  }

  def default[U: Manifest]    = new DefaultResponseHandler[U]
  def failure404[U: Manifest] = new NotFound404ResponseHandler[U]
}

// standard response handler, 200-204s are ok, and everything else is marhalled into an error
class DefaultResponseHandler[U: Manifest] extends ResponseHandler[U] {
  override def handle(response: HttpResponse): Either[ElasticError, U] = response.statusCode match {
    case 200 | 201 | 202 | 203 | 204 =>
      val entity = response.entity.getOrError("No entity defined")
      Right(ResponseHandler.fromEntity[U](entity))
    case _ =>
      Left(ElasticError.parse(response))
  }
}

class NotFound404ResponseHandler[U: Manifest] extends DefaultResponseHandler[U] {
  override def handle(response: HttpResponse): Either[ElasticError, U] =
    response.statusCode match {
      case 404 | 500 => sys.error(response.toString)
      case _         => super.handle(response)
    }
}

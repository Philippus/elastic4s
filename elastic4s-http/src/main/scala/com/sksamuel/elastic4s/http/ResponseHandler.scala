package com.sksamuel.elastic4s.http

import java.nio.charset.Charset

import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.elastic4s.json.JacksonSupport
import com.sksamuel.exts.Logging

import scala.io.Codec
import scala.util.{Failure, Try}

trait ResponseHandler[U] {
  def handle(response: HttpResponse): Try[U] = Try(doit(response))
  def doit(response: HttpResponse): U = ???
  protected def handleError(response: HttpResponse): Failure[U] = Failure(new RuntimeException(response.entity.map(_.content).getOrElse("no error message")))
}

// a ResponseHandler that marshalls the body into the required type using Jackson
// the response body is converted into a string using a codec derived from the content encoding header
// if the content encoding header is null, then UTF-8 is assumed
object ResponseHandler extends Logging {

  def json(entity: HttpEntity.StringEntity): JsonNode = fromEntity[JsonNode](entity)

  def fromNode[U: Manifest](node: JsonNode): U = {
    logger.debug(s"Attempting to unmarshall json node to ${manifest.runtimeClass.getName}")
    implicit val codec = Codec(Charset.defaultCharset)
    JacksonSupport.mapper.readValue[U](JacksonSupport.mapper.writeValueAsBytes(node))
  }

  def fromResponse[U: Manifest](response: HttpResponse): U = fromEntity(response.entity.get)

  def fromEntity[U: Manifest](entity: HttpEntity.StringEntity): U = {
    logger.debug(s"Attempting to unmarshall response to ${manifest.runtimeClass.getName}")
    val charset = entity.contentType.getOrElse("UTF-8")
    implicit val codec = Codec(Charset.forName(charset))
    logger.debug(entity.content)
    JacksonSupport.mapper.readValue[U](entity.content)
  }

  def default[U: Manifest] = new DefaultResponseHandler[U]
  def failure404[U: Manifest] = new NotFound404ResponseHandler[U]
}

class DefaultResponseHandler[U: Manifest] extends ResponseHandler[U] {
  override def handle(response: HttpResponse): Try[U] = Try(ResponseHandler.fromEntity[U](response.entity.get))
}

class NotFound404ResponseHandler[U: Manifest] extends DefaultResponseHandler[U] {
  override def handle(response: HttpResponse): Try[U] = {
    response.statusCode match {
      case 404 | 500 => Failure(new RuntimeException(response.entity.map(_.content).getOrElse("no error message")))
      case _ => super.handle(response)
    }
  }
}

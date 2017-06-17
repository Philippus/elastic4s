package com.sksamuel.elastic4s.http

import java.nio.charset.Charset

import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.elastic4s.json.JacksonSupport
import com.sksamuel.exts.Logging
import org.apache.http.HttpEntity
import org.elasticsearch.client.{Response, ResponseException}

import scala.io.{Codec, Source}
import scala.util.{Failure, Try}

trait ResponseHandler[U] {
  def onResponse(response: Response): Try[U]
  def onError(e: Throwable): Try[U] = Failure(e)
}

// a ResponseHandler that marshalls the body into the required type using Jackson
// the response body is converted into a string using a codec derived from the content encoding header
// if the content encoding header is null, then UTF-8 is assumed
object ResponseHandler extends Logging {

  def json(entity: HttpEntity): JsonNode = fromEntity[JsonNode](entity)

  def fromEntity[U](entity: HttpEntity, klass: Class[U]): U = {
    logger.debug(s"Attempting to unmarshall response to ${klass.getName}")
    val charset = Option(entity.getContentEncoding).map(_.getValue).getOrElse("UTF-8")
    implicit val codec = Codec(Charset.forName(charset))
    val body = Source.fromInputStream(entity.getContent).mkString
    logger.debug(body)
    JacksonSupport.mapper.readValue(body, klass)
  }

  def fromEntity[U: Manifest](entity: HttpEntity): U = fromEntity(entity, manifest.runtimeClass.asInstanceOf[Class[U]])

  def default[U: Manifest] = new DefaultResponseHandler[U]
  def failure404[U: Manifest] = new NotFound404ResponseHandler[U]
}

class DefaultResponseHandler[U: Manifest] extends ResponseHandler[U] {
  override def onResponse(response: Response): Try[U] = Try(ResponseHandler.fromEntity[U](response.getEntity))
  override def onError(e: Throwable): Try[U] = Failure(e)
}

class NotFound404ResponseHandler[U: Manifest] extends DefaultResponseHandler[U] {
  override def onError(e: Throwable): Try[U] = {
    e match {
      case re: ResponseException if re.getResponse.getStatusLine.getStatusCode == 404 =>
        Try(ResponseHandler.fromEntity[U](re.getResponse.getEntity))
      case _ => Failure(e)
    }
  }
}

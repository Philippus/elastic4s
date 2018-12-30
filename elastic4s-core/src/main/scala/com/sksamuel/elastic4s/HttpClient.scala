package com.sksamuel.elastic4s

import java.io.{File, InputStream}
import java.nio.file.Files

import com.sksamuel.exts.Logging

import scala.io.Source

/**
  * Adapts an underlying http client so that it can be used by the elastic client.
  */
trait HttpClient extends Logging {

  /**
    * Sends the given request to elasticsearch.
    *
    * Implementations should invoke the callback function once the response is known.
    *
    * The callback function should be invoked with a HttpResponse for all requests that received
    * a response, including 4xx and 5xx responses. The callback function should only be invoked
    * with an exception if the client failed.
    */
  def send(request: ElasticRequest, callback: Either[Throwable, HttpResponse] => Unit): Unit

  /**
    * Closes the underlying http client. Can be a no-op if the underlying client does not have
    * state that needs to be closed
    */
  def close(): Unit
}

case class HttpResponse(statusCode: Int, entity: Option[HttpEntity.StringEntity], headers: Map[String, String])

sealed trait HttpEntity {
  def contentCharset: Option[String]
  def get: String
}

object HttpEntity {

  def apply(content: String): HttpEntity                      = HttpEntity(content, "application/json; charset=utf-8")
  def apply(content: String, contentType: String): HttpEntity = StringEntity(content, Some(contentType))

  case class StringEntity(content: String, contentCharset: Option[String]) extends HttpEntity {
    def get: String = content
  }

  case class InputStreamEntity(content: InputStream, contentCharset: Option[String]) extends HttpEntity {
    def get: String = Source.fromInputStream(content).getLines().mkString("\n")
  }

  case class FileEntity(content: File, contentCharset: Option[String]) extends HttpEntity {

    import scala.collection.JavaConverters._

    def get: String = Files.readAllLines(content.toPath).asScala.mkString("\n")
  }
}

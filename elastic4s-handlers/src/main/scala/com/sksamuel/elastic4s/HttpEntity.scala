package com.sksamuel.elastic4s

import java.io.{File, InputStream}
import java.nio.file.Files
import scala.io.Source

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

  case class ByteArrayEntity(content: Array[Byte], contentCharset: Option[String]) extends HttpEntity {
    def get: String = new String(content, contentCharset.getOrElse("utf-8"))
  }
}

sealed trait HttpEntity {
  def contentCharset: Option[String]
  def get: String
}

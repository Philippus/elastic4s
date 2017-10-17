package com.sksamuel.elastic4s.aws

import java.net.{ URI, URLEncoder }
import java.nio.charset.Charset

import org.apache.http.{ HttpEntityEnclosingRequest, HttpRequest }
import org.apache.http.client.methods.HttpRequestWrapper
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.util.EntityUtils

import scala.collection.JavaConverters._

import com.sksamuel.elastic4s.aws.Crypto._

object CanonicalRequest {

  private val ignoredHeaders = List("connection", "content-length")

  def apply(httpRequest: HttpRequest): CanonicalRequest = {
    val method = httpRequest.getRequestLine.getMethod
    val uri = canonicalUri(httpRequest)
    val query = canonicalQueryString(httpRequest)
    val headers = canonicalHeaders(httpRequest)
    val signed = signedHeaders(httpRequest)
    val payload = hashedPayload(httpRequest)
    CanonicalRequest(method, uri, query, headers, signed, payload)
  }

  private def canonicalUri(httpRequest: HttpRequest): String = {
    val uri = new URI(httpRequest.getRequestLine.getUri)
    val path = uri.getPath
    val segments = path.split("/")
    segments.filter(_ != "").map(URLEncoder.encode(_, "utf-8")).mkString(start = "/", sep = "/", end = "")
  }

  private def canonicalQueryString(httpRequest: HttpRequest): String = {
    val uri  = new URI(httpRequest.getRequestLine.getUri)
    val charset : Charset = Charset.forName("utf-8")
    val parameters = URLEncodedUtils.parse(uri, "utf-8")
    parameters.asScala.map(h ⇒ s"${h.getName}=${h.getValue}").mkString("&")
  }

  private def canonicalHeaders(httpRequest: HttpRequest): String =
    httpRequest.getAllHeaders().
      sortBy(_.getName.toLowerCase).
      filterNot(h ⇒ ignoredHeaders.contains(h.getName.toLowerCase)).
      map(h ⇒ s"${h.getName.toLowerCase}:${h.getValue.trim}").mkString("\n")

  private def signedHeaders(httpRequest: HttpRequest): String =
    httpRequest.getAllHeaders.
      map(_.getName.toLowerCase).
      filterNot(ignoredHeaders.contains(_)).
      sorted.mkString(";")

  private def hashedPayload(httpRequest: HttpRequest): String = {
    def hashPayloadString(str: String) = {
      val hashedPayload = hash(str)
      hexOf(hashedPayload).toLowerCase()
    }

    getPayload(httpRequest) match {
      case Some(payload) ⇒ hashPayloadString(payload)
      case None          ⇒ hashPayloadString("")
    }
  }

  private def getPayload(httpRequest: HttpRequest): Option[String] = {

    lazy val entity = httpRequest.asInstanceOf[HttpEntityEnclosingRequest].getEntity
    val request = HttpRequestWrapper.wrap(httpRequest)

    if (!classOf[HttpEntityEnclosingRequest].isAssignableFrom(request.getClass) || entity == null) None
    else Option(EntityUtils.toString(entity))
  }
}

case class CanonicalRequest(
                             method: String,
                             canonicalUri: String,
                             canonicalQueryString: String,
                             canonicalHeaders: String,
                             signedHeaders: String,
                             hashedPayload: String) {

  override def toString() =
    s"$method\n$canonicalUri\n$canonicalQueryString\n$canonicalHeaders\n\n$signedHeaders\n$hashedPayload"

  def toHashString() = {
    val canonicalRequestHash = hash(toString)
    hexOf(canonicalRequestHash)
  }
}

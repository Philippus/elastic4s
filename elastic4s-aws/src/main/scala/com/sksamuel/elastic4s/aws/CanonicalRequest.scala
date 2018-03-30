package com.sksamuel.elastic4s.aws

import com.sksamuel.elastic4s.aws.Crypto._
import java.net.{URI, URLEncoder}

import org.apache.http.{HttpEntityEnclosingRequest, HttpRequest}
import org.apache.http.client.methods.HttpRequestWrapper
import org.apache.http.client.utils.{URIBuilder, URLEncodedUtils}
import org.apache.http.util.EntityUtils

import scala.collection.JavaConverters._

/**
  * Canonical Request is described as the first task when signing aws requests (version 4)
  * See <a href="http://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html">canonical request documentation</a>
  *
  * In summary, the canonical request is a string formed by the concatenation of the result of the following steps.
  *
  * <ul>
  * <li> Request method, upper case;
  * <li> Uri in a canonical format (absolute path component of the URI);
  * <li> Query string in a canonical format;
  * <li> Headers to sign in a canonical format (key=value&);
  * <li> Concatenation of the headers to sign;
  * <li> Hash of the request payload (hash of empty string if none is found);
  * </ul>
  */
object CanonicalRequest {

  private val ignoredHeaders = List("connection", "content-length")

  def apply(httpRequest: HttpRequest): CanonicalRequest = {
    val method  = httpRequest.getRequestLine.getMethod
    val uri     = canonicalUri(httpRequest)
    val query   = canonicalQueryString(httpRequest)
    val headers = canonicalHeaders(httpRequest)
    val signed  = signedHeaders(httpRequest)
    val payload = hashedPayload(httpRequest)
    CanonicalRequest(method, uri, query, headers, signed, payload)
  }

  private def canonicalUri(httpRequest: HttpRequest): String = {

    val uri = new URIBuilder()
      .setPath(httpRequest.getRequestLine.getUri)
      .build()
      .getPath

    val path = uri.split("\\?")(0) //using URIBuilder to normalize uri but need to split manually
    path
      .split("(?<!/)/(?!/)", -1)
      .map(URLEncoder.encode(_, "utf-8"))
      .mkString(start = "", sep = "/", end = "")
      .replace("*", "%2A")
  }

  private def canonicalQueryString(httpRequest: HttpRequest): String = {
    val uri        = new URI(httpRequest.getRequestLine.getUri)
    val parameters = URLEncodedUtils.parse(uri, "utf-8")
    parameters.asScala.sortBy(_.getName).map(h ⇒ s"${h.getName}=${h.getValue}").mkString("&")
  }

  private def canonicalHeaders(httpRequest: HttpRequest): String =
    httpRequest
      .getAllHeaders()
      .sortBy(_.getName.toLowerCase)
      .filterNot(h ⇒ ignoredHeaders.contains(h.getName.toLowerCase))
      .map(h ⇒ s"${h.getName.toLowerCase}:${h.getValue.trim}")
      .mkString("\n")

  private def signedHeaders(httpRequest: HttpRequest): String =
    httpRequest.getAllHeaders.map(_.getName.toLowerCase).filterNot(ignoredHeaders.contains(_)).sorted.mkString(";")

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
    val request     = HttpRequestWrapper.wrap(httpRequest)

    if (!classOf[HttpEntityEnclosingRequest].isAssignableFrom(request.getClass) || entity == null) None
    else Option(EntityUtils.toString(entity))
  }
}

case class CanonicalRequest(method: String,
                            canonicalUri: String,
                            canonicalQueryString: String,
                            canonicalHeaders: String,
                            signedHeaders: String,
                            hashedPayload: String) {

  override def toString() =
    s"""$method
       |$canonicalUri
       |$canonicalQueryString
       |$canonicalHeaders
       |
       |$signedHeaders
       |$hashedPayload""".stripMargin

  def toHashString() = {
    val canonicalRequestHash = hash(toString)
    hexOf(canonicalRequestHash)
  }
}

package com.sksamuel.elastic4s.aws

import java.net.{ URI, URLEncoder }
import java.nio.charset.Charset

import org.apache.http.{ HttpEntityEnclosingRequest, HttpRequest }
import org.apache.http.client.methods.HttpRequestWrapper
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.util.EntityUtils

import scala.collection.JavaConverters._

import com.sksamuel.elastic4s.aws.Crypto._

/**
  * Canonical Request is described as the first task when signing aws requests (version 4)
  * See <a href="http://docs.aws.amazon.com/general/latest/gr/sigv4-create-canonical-request.html">canonical request documentation</a>
  *
  * In summary, the canonical request is a string formed by the concatenation of the result of several steps.
  *
  * <ul>
  * <li> Request method, upper case;
  * <li> Uri in a canonical format (absolute path component of the URI);
  * <li> Query string in a canonical format;
  * <li> Headers to sign in a canonical format;
  * <li> Concatenation of the headers to sign;
  * <li> Hash of the request payload (empty string if none is found);
  * </ul>
  */
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
      s"$method\n" +
      s"$canonicalUri\n" +
      s"$canonicalQueryString\n" +
      s"$canonicalHeaders\n\n" +
      s"$signedHeaders\n" +
      s"$hashedPayload"

  def toHashString() = {
    val canonicalRequestHash = hash(toString)
    hexOf(canonicalRequestHash)
  }
}

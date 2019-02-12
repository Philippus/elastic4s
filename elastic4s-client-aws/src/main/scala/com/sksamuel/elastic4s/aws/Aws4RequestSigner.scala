package com.sksamuel.elastic4s.aws

import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, ZonedDateTime}

import software.amazon.awssdk.auth.credentials.{AwsCredentials, AwsCredentialsProvider, AwsSessionCredentials}
import com.sksamuel.elastic4s.aws.Crypto._
import org.apache.http.{Header, HttpRequest}
import software.amazon.awssdk.regions.Region

/**
  * AWS request signer (version 4) that follows the documentation given by amazon
  * See <a href="http://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html">request signing documentation</a>
  *
  * @param provider - an implementation of com.amazonaws.auth.AwsCredentialsProvider capable of providing aws user credentials
  * @param region   - amazon region (i.e. eu-west-1)
  * @param service  - service requested, in this context, should always be elastic search, identified by the string "es"
  */
class Aws4RequestSigner(provider: AwsCredentialsProvider, region: Region, service: String = "es") {
  require(provider.resolveCredentials != null, "AWS Credentials are mandatory. AwsCredentialsProvider provided none.")

  val dateHeader          = "X-Amz-Date"
  val authHeader          = "Authorization"
  val securityTokenHeader = "X-Amz-Security-Token"

  def withAws4Headers(request: HttpRequest): HttpRequest = {

    val credentials = provider.resolveCredentials

    val (date, dateTime) = buildDateAndDateTime()

    setHostHeader(request)

    /* Insert aws date time header*/
    request.setHeader(dateHeader, dateTime)

    val canonicalRequest = CanonicalRequest(request)
    val stringToSign     = StringToSign(service, region, canonicalRequest, date, dateTime)

    val authHeaderValue = buildAuthenticationHeader(canonicalRequest, stringToSign, credentials)
    request.addHeader(authHeader, authHeaderValue)

    /* If the credentials are temporary (session credentials), add an additional security header */
    credentials match {
      case c: AwsSessionCredentials ⇒ request.addHeader(securityTokenHeader, c.sessionToken)
      case _                        ⇒
    }

    request
  }

  /* Build date and dateTime in a protected method so it is possible to override it in tests */
  protected def buildDateAndDateTime(): (String, String) = {
    val now      = ZonedDateTime.now(ZoneOffset.UTC)
    val dateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"))
    val date     = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    (date, dateTime)
  }

  private def buildAuthenticationHeader(canonicalRequest: CanonicalRequest,
                                        stringToSign: StringToSign,
                                        credentials: AwsCredentials) = {
    val credentialStr    = s"Credential=${credentials.accessKeyId}/${stringToSign.credentialsScope}"
    val signedHeadersStr = s"SignedHeaders=${canonicalRequest.signedHeaders}"
    val signatureStr     = s"Signature=${buildSignature(stringToSign, credentials)}"
    s"${Crypto.Algorithm} $credentialStr, $signedHeadersStr, $signatureStr"
  }

  private def buildSignature(stringToSign: StringToSign, credentials: AwsCredentials) = {
    val signatureKey = buildKeyToSign(stringToSign.date, credentials)
    val signature    = sign(stringToSign.toString, signatureKey)
    hexOf(signature).toLowerCase
  }

  private def buildKeyToSign(dateStr: String, credentials: AwsCredentials): Array[Byte] = {
    val kSecret    = ("AWS4" + credentials.secretAccessKey).getBytes("utf-8")
    val dateKey    = sign(dateStr, kSecret)
    val regionKey  = sign(region.id, dateKey)
    val serviceKey = sign(service, regionKey)
    sign("aws4_request", serviceKey)
  }

  /* If host header is not found: should create new  Host header. Currently could not retrieve host from Apache HttpRequest.*/
  private def setHostHeader: HttpRequest => HttpRequest = {
    val found = (header: Header) => header.getValue.replaceFirst(":[0-9]+", "")
    setHeader("Host", found)
  }

  private def setHeader(h: String, f: Header => String)(request: HttpRequest): HttpRequest = {
    request.getAllHeaders.find(_.getName == h) match {
      case Some(header) ⇒ request.setHeader(h, f(header))
      case _            ⇒
    }
    request
  }
}

package com.sksamuel.elastic4s.aws

import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, ZonedDateTime}

import com.amazonaws.auth.{AWSCredentialsProvider, AWSSessionCredentials}
import org.apache.http.HttpRequest

import com.sksamuel.elastic4s.aws.Crypto._

/**
  * AWS request signer (version 4) that follows the documentation given by amazon
  * See <a href="http://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html">request signing documentation</a>
  *
  * @param awsCredentialProvider - capable of providing credentials
  * @param region - amazon region (i.e. eu-west-1)
  * @param service - service requested, in this context, should always be elastic search, identified by the string "es"
  */
class Aws4RequestSigner(awsCredentialProvider: AWSCredentialsProvider, region: String, service: String = "es") {

  require(awsCredentialProvider.getCredentials != null, "AWS Credentials are mandatory. AWSCredentialsProvider provided none.")

  val credentials = awsCredentialProvider.getCredentials
  val dateHeader = "X-Amz-Date"
  val authHeader = "Authorization"
  val securityTokenHeader = "X-Amz-Security-Token"

  def withAws4Headers(request: HttpRequest): HttpRequest = {

    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val dateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"))
    val date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

    request.addHeader(dateHeader, dateTime)
    cleanHostHeader(request)

    val canonicalRequest = CanonicalRequest(request)
    val stringToSign = StringToSign(service, region, canonicalRequest, date, dateTime)

    val headerValue = buildAuthenticationHeader(canonicalRequest, stringToSign)
    request.addHeader(authHeader, headerValue)

    /* If the credentials are temporary (session credentials), add an additional security header */
    credentials match {
      case c: AWSSessionCredentials ⇒ request.addHeader(securityTokenHeader, c.getSessionToken)
      case _                        ⇒
    }

    request
  }

  private def buildAuthenticationHeader(canonicalRequest: CanonicalRequest, stringToSign: StringToSign) = {
    val credentialStr = s"Credential=${credentials.getAWSAccessKeyId}/${stringToSign.credentialsScope}"
    val signedHeadersStr = s"SignedHeaders=${canonicalRequest.signedHeaders}"
    val signatureStr = s"Signature=${buildSignature(stringToSign)}"
    s"${Crypto.Algorithm} $credentialStr, $signedHeadersStr, $signatureStr"
  }

  private def buildSignature(stringToSign: StringToSign) = {
    val signatureKey = buildKeyToSign(stringToSign.date)
    val signature = sign(stringToSign.toString, signatureKey)
    hexOf(signature).toLowerCase
  }

  private def buildKeyToSign(dateStr: String): Array[Byte] = {
    val kSecret = ("AWS4" + credentials.getAWSSecretKey).getBytes("utf-8")
    val dateKey = sign(dateStr, kSecret)
    val regionKey = sign(region, dateKey)
    val serviceKey = sign(service, regionKey)
    sign("aws4_request", serviceKey)
  }

  private def cleanHostHeader(req: HttpRequest): Unit = {
    req.getAllHeaders.find(_.getName == "Host") match {
      case Some(header) ⇒
        val value = header.getValue.replaceFirst(":[0-9]+", "")
        req.setHeader("Host", value)
      case _            ⇒
    }
  }
}

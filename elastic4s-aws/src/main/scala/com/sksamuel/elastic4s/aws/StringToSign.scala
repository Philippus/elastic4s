package com.sksamuel.elastic4s.aws

import com.sksamuel.elastic4s.aws.Crypto._

/**
  * String to sign is described as the second task when signing aws requests (version 4).
  * See <a href="http://docs.aws.amazon.com/general/latest/gr/sigv4-create-string-to-sign.html">String to sign documentation</a>
  */
case class StringToSign(service: String,
                        region: String,
                        canonicalRequest: CanonicalRequest,
                        date: String,
                        dateTime: String) {

  val credentialsScope = s"$date/$region/$service/aws4_request"

  override def toString(): String =
    s"$Algorithm\n" +
      s"$dateTime\n" +
      s"$credentialsScope\n" +
      s"${canonicalRequest.toHashString.toLowerCase}"
}

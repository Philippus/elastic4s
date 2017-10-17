package com.sksamuel.elastic4s.aws

case class StringToSign(
    service: String,
    region: String,
    canonicalRequest: CanonicalRequest,
    date: String,
    dateTime: String) {

  val credentialsScope = s"$date/$region/$service/aws4_request"

  override def toString(): String =
    s"${Crypto.Algorithm}\n" +
      s"$dateTime\n" +
      s"$credentialsScope\n" +
      s"${canonicalRequest.toHashString.toLowerCase}"
}

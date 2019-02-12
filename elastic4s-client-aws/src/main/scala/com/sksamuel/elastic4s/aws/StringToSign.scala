package com.sksamuel.elastic4s.aws

import com.sksamuel.elastic4s.aws.Crypto._
import software.amazon.awssdk.regions.Region

/**
  * String to sign is described as the second task when signing aws requests (version 4).
  * See <a href="http://docs.aws.amazon.com/general/latest/gr/sigv4-create-string-to-sign.html">String to sign documentation</a>
  */
case class StringToSign(service: String,
                        region: Region,
                        canonicalRequest: CanonicalRequest,
                        date: String,
                        dateTime: String) {

  val credentialsScope = s"$date/${region.id}/$service/aws4_request"

  override def toString(): String =
    s"""$Algorithm
       |$dateTime
       |$credentialsScope
       |${canonicalRequest.toHashString.toLowerCase}""".stripMargin
}

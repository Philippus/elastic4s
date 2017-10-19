package com.sksamuel.elastic4s.aws

import com.amazonaws.auth._
import org.scalatest.{Matchers, WordSpec}

class Aws4RequestSignerTest extends WordSpec with Matchers with SharedTestData {

  "Aws4RequestSigner" should {

    val result =
      """AWS4-HMAC-SHA256 Credential=AKIDEXAMPLE/20150830/us-east-1/es/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=6aab780be5a6c06d0a7cc4528756dae4394e3c99b5d81bd4b07a745ab5a90735""".stripMargin

    "be able to add amazon compliant authentication header" in {

      val chainProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsKey, awsSecret))
      val signer = new Aws4TestRequestSigner(chainProvider, region, date, dateTime)
      val withHeaders = signer.withAws4Headers(httpPostRequest)
      withHeaders.getAllHeaders find (_.getName == "Authorization") match {
        case Some(header) ⇒ header.getValue shouldBe (result)
        case _            ⇒ 1 shouldBe (0)
      }
    }

    "be able to add security Token Header if there is a session key in context" in {
      val credentials = new BasicSessionCredentials(awsKey, awsSecret, awsSessionToken)
      val chainProvider = new AWSStaticCredentialsProvider(credentials)
      val signer = new Aws4TestRequestSigner(chainProvider, region, date, dateTime)
      val withHeaders = signer.withAws4Headers(httpPostRequest)
      withHeaders.getAllHeaders find (_.getName == "X-Amz-Security-Token") match {
        case Some(header) ⇒ header.getValue shouldBe (awsSessionToken)
        case _            ⇒ 1 shouldBe (0)
      }
    }
  }

  class Aws4TestRequestSigner(awsCredentialProvider: AWSCredentialsProvider, region: String, date: String, dateTime: String)
    extends Aws4RequestSigner(awsCredentialProvider, region) {
    override def buildDateAndDateTime() = (date, dateTime)
  }

}



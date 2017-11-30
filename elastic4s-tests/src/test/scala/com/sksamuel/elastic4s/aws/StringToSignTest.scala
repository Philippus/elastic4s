package com.sksamuel.elastic4s.aws

import org.scalatest.{Matchers, WordSpec}

class StringToSignTest extends WordSpec with Matchers with SharedTestData{

  "StringToSign" should {

    val result =
      """AWS4-HMAC-SHA256
        |20150830T123600Z
        |20150830/us-east-1/es/aws4_request
        |5fab998086fcbea8299a5c08a7698e48dbb67a6c4aa91276acaef121cd40edec""".stripMargin

    "be able to build instance from region, service and canonicalRequest " in {

      val canonicalRequest = CanonicalRequest(httpGetRequest)
      val stringToSign = StringToSign(service, region, canonicalRequest, date, dateTime)
      stringToSign.toString shouldBe (result)
    }
  }
}

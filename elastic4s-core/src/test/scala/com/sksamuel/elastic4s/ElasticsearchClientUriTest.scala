package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, Matchers}

class ElasticsearchClientUriTest extends FlatSpec with Matchers {

  "elasticsearch uri" should "parse multiple host/ports" in {
    val uri = ElasticsearchClientUri("elasticsearch://host1:1234,host2:2345")
    uri.hosts shouldBe List("host1" -> 1234, "host2" -> 2345)
  }

  it should "parse single host/ports" in {
    val uri = ElasticsearchClientUri("elasticsearch://host1:1234")
    uri.hosts shouldBe List("host1" -> 1234)
  }

  it should "errors on trailing commas" in {
    val uri = ElasticsearchClientUri("elasticsearch://host1:1234,")
    uri.hosts shouldBe List("host1" -> 1234)
  }

  it should "parse options" in {
    ElasticsearchClientUri("elasticsearch://host1:1234,host2:9999?a=b&c=d").options shouldBe
      Map("a" -> "b", "c" -> "d")
  }

  it should "error on missing values between commas" in {
    intercept[IllegalArgumentException] {
      ElasticsearchClientUri("elasticsearch://host1:1234,,host2:9999")
    } should not be null
  }
}

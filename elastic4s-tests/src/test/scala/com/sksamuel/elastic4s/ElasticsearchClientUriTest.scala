package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, Matchers}

class ElasticsearchClientUriTest extends FlatSpec with Matchers {

  private def testString(connectionString: String,
                         hosts: List[(String, Int)],
                         options: Map[String, String] = Map.empty): Unit = {
    val uri = ElasticsearchClientUri(connectionString)
    uri.hosts shouldBe hosts
    uri.options shouldBe options
  }

  "elasticsearch uri" should "parse multiple host/ports" in {
    testString("elasticsearch://host1:1234,host2:2345", List("host1" -> 1234, "host2" -> 2345))
  }

  it should "parse single host/ports" in {
    testString("elasticsearch://host1:1234", List("host1" -> 1234))
  }

  it should "parse single host/ports with trailing slash" in {
    testString("elasticsearch://host1:1234/", List("host1" -> 1234))
  }

  it should "errors on trailing commas" in {
    testString("elasticsearch://host1:1234,", List("host1" -> 1234))
  }

  it should "parse everything" in {
    testString("elasticsearch://host1:1234,host2:9999?a=b&c=d",
      List(("host1", 1234), ("host2", 9999)),
      Map("a" -> "b", "c" -> "d"))
  }

  it should "parse everything with trailing slash" in {
    testString("elasticsearch://host1:1234,host2:9999/?a=b&c=d",
      List(("host1", 1234), ("host2", 9999)),
      Map("a" -> "b", "c" -> "d"))
  }

  it should "parse options" in {
    ElasticsearchClientUri("elasticsearch://host1:1234,host2:9999?a=b&c=d").options shouldBe Map("a" -> "b", "c" -> "d")
  }

  it should "error on missing values between commas" in {
    intercept[RuntimeException] {
      ElasticsearchClientUri("elasticsearch://host1:1234,,host2:9999")
    } should not be null
  }

  it should "support http protocol" in {
    testString("http://host1:1234,host2:2345", List("host1" -> 1234, "host2" -> 2345))
  }
}

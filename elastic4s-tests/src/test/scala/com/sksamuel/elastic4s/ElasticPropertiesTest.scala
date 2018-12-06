package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.http.{ElasticNodeEndpoint, ElasticProperties}
import org.scalatest.{FlatSpec, Matchers}

class ElasticPropertiesTest extends FlatSpec with Matchers {

  "elasticsearch properties" should "parse multiple host/ports" in {
    ElasticProperties("http://host1:1234,host2:2345") shouldBe
      http.ElasticProperties(Seq(ElasticNodeEndpoint("http", "host1", 1234), ElasticNodeEndpoint("http", "host2", 2345)))
  }

  it should "parse single host/ports" in {
    ElasticProperties("http://host1:1234") shouldBe http.ElasticProperties(Seq(ElasticNodeEndpoint("http", "host1", 1234)))
  }

  it should "parse single host/ports with trailing slash" in {
    ElasticProperties("http://host1:1234/") shouldBe http.ElasticProperties(Seq(ElasticNodeEndpoint("http", "host1", 1234)))
  }

  it should "parse single host/ports with auth" in {
    ElasticProperties("http://user:pass@host1:1234") shouldBe http.ElasticProperties(Seq(ElasticNodeEndpoint("http", "user:pass@host1", 1234)))
  }

  it should "errors on invalid host string" in {
    intercept[RuntimeException] {
      ElasticsearchClientUri("elasticsearch://host1:1234,qwe")
    } should not be null
  }

  it should "parse everything" in {
    ElasticProperties("http://user:pass@host1:1234?a=b&c=d") shouldBe
      http.ElasticProperties(Seq(ElasticNodeEndpoint("http", "user:pass@host1", 1234)), None, Map("a" -> "b", "c" -> "d"))
  }

  it should "parse everything with trailing slash" in {
    ElasticProperties("http://user:pass@host1:1234/?a=b&c=d") shouldBe
      http.ElasticProperties(Seq(ElasticNodeEndpoint("http", "user:pass@host1", 1234)), None, Map("a" -> "b", "c" -> "d"))
  }

  it should "error on missing values between commas" in {
    intercept[RuntimeException] {
      ElasticsearchClientUri("elasticsearch://host1:1234,,host2:9999")
    } should not be null
  }

  it should "support https protocol" in {
    ElasticProperties("https://host1:1234,host2:2345") shouldBe
      http.ElasticProperties(Seq(ElasticNodeEndpoint("https", "host1", 1234), ElasticNodeEndpoint("https", "host2", 2345)))
  }

  it should "support prefix path" in {
    ElasticProperties("https://host1:1234,host2:2345/prefix/path") shouldBe
      http.ElasticProperties(Seq(ElasticNodeEndpoint("https", "host1", 1234), ElasticNodeEndpoint("https", "host2", 2345)), Some("/prefix/path"))
  }

  it should "parse multiple hosts and a global prefix" in {
    val expectedEndpoints = Seq(
      ElasticNodeEndpoint("https", "host1", 11),
      ElasticNodeEndpoint("https", "host2", 22),
      ElasticNodeEndpoint("https", "host3", 33)
    )

    ElasticProperties("https://host1:11,host2:22,host3:33/prefixGlobal?fizz=buzz") shouldBe
      http.ElasticProperties(expectedEndpoints, Some("/prefixGlobal"), Map("fizz" -> "buzz"))
  }

  it should "support prefix path with trailing slash" in {
    ElasticProperties("https://host1:1234,host2:2345/prefix/path/") shouldBe
      http.ElasticProperties(Seq(ElasticNodeEndpoint("https", "host1", 1234), ElasticNodeEndpoint("https", "host2", 2345)), Some("/prefix/path"))
  }

  it should "support prefix path with options" in {
    ElasticProperties("https://host1:1234,host2:2345/prefix/path?a=b&c=d") shouldBe
      http.ElasticProperties(
        Seq(
          ElasticNodeEndpoint("https", "host1", 1234),
          ElasticNodeEndpoint("https", "host2", 2345)
        ),
        Some("/prefix/path"),
        Map("a" -> "b", "c" -> "d"))
  }

  it should "support prefix path with trailing slash and options" in {
    ElasticProperties("https://host1:1234,host2:2345/prefix/path/?a=b&c=d") shouldBe
      http.ElasticProperties(Seq(ElasticNodeEndpoint("https", "host1", 1234), ElasticNodeEndpoint("https", "host2", 2345)), Some("/prefix/path"), Map("a" -> "b", "c" -> "d"))
  }
}

package com.sksamuel.elastic4s.http

import java.net.SocketException

import com.sksamuel.elastic4s.ElasticsearchClientUri
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration.Duration

class HttpClientTest extends FlatSpec with Matchers with ElasticDsl {

  "HttpClient" should "throw an error when it cannot connect" in {
    intercept[SocketException] {
      val client = HttpClient(ElasticsearchClientUri("123", 1))
      client.execute {
        indexInto("a-index" / "a-type") id "a-id" fields Map("wibble" -> "foo")
      }.await(Duration.Inf)
    }
  }
}

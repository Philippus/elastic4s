package com.sksamuel.elastic4s.http

import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicReference

import com.sksamuel.elastic4s.{ElasticProperties, ElasticRequest, HttpEntity}
import com.sun.net.httpserver.{HttpExchange, HttpServer}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class JavaClientCompressionTest extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  private case class Received(contentEncoding: Option[String], rawBody: Array[Byte])

  private val received = new AtomicReference[Received]()

  private val server = HttpServer.create(new InetSocketAddress("localhost", 0), 0)
  server.createContext(
    "/",
    (exchange: HttpExchange) => {
      val body     = exchange.getRequestBody.readAllBytes()
      received.set(Received(Option(exchange.getRequestHeaders.getFirst("Content-Encoding")), body))
      val response = """{"acknowledged":true}""".getBytes("UTF-8")
      exchange.getResponseHeaders.add("Content-Type", "application/json")
      exchange.sendResponseHeaders(200, response.length.toLong)
      exchange.getResponseBody.write(response)
      exchange.close()
    }
  )

  override protected def beforeAll(): Unit = server.start()
  override protected def afterAll(): Unit  = server.stop(0)

  private val payload = "{\"index\":{}}\n" + ("{\"field\":\"value\"}\n" * 200)

  private def send(compressionEnabled: Boolean): Received = {
    val props  = ElasticProperties(s"http://localhost:${server.getAddress.getPort}")
    val client = JavaClient(props, NoOpRequestConfigCallback, NoOpHttpClientConfigCallback, compressionEnabled)
    try {
      val request = ElasticRequest("POST", "/_bulk", HttpEntity(payload))
      Await.result(client.send(request), 10.seconds).statusCode shouldBe 200
      received.get()
    } finally Await.result(client.close(), 10.seconds)
  }

  "JavaClient" should "gzip request bodies when compression is enabled" in {
    val r            = send(compressionEnabled = true)
    r.contentEncoding shouldBe Some("gzip")
    val decompressed = new String(
      new java.util.zip.GZIPInputStream(new java.io.ByteArrayInputStream(r.rawBody)).readAllBytes(),
      "UTF-8"
    )
    decompressed shouldBe payload
    r.rawBody.length should be < payload.length
  }

  it should "not compress request bodies by default" in {
    val r = send(compressionEnabled = false)
    r.contentEncoding shouldBe None
    new String(r.rawBody, "UTF-8") shouldBe payload
  }
}

package com.sksamuel.elastic4s.akka

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import com.sksamuel.elastic4s.{ElasticRequest, HttpEntity => ElasticEntity, HttpResponse => ElasticResponse}
import org.mockito.ArgumentMatchers._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class AkkaHttpClientMockTest
  extends AnyWordSpec
    with Matchers
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience
    with BeforeAndAfterAll {

  private implicit lazy val system: ActorSystem = ActorSystem()

  override def afterAll: Unit = {
    system.terminate()
  }

  def mockHttpPool(): (Function[HttpRequest, Try[HttpResponse]], TestHttpPoolFactory) = {
    val sendRequest = mock[Function[HttpRequest, Try[HttpResponse]]]
    val poolFactory = new TestHttpPoolFactory(sendRequest)
    (sendRequest, poolFactory)
  }

  "AkkaHttpClient" should {

    "retry on 502" in {

      val hosts = List(
        "host1",
        "host2"
      )

      val blacklist = mock[Blacklist]

      val (sendRequest, httpPool) = mockHttpPool()

      val client =
        new AkkaHttpClient(AkkaHttpClientSettings(hosts), blacklist, httpPool)

      when(blacklist.contains("host1")).thenReturn(false)
      when(blacklist.contains("host2")).thenReturn(false)
      when(blacklist.add("host1")).thenReturn(true)
      when(blacklist.remove("host2")).thenReturn(false)

      when(sendRequest
        .apply(argThat { (r: HttpRequest) =>
          r != null && r.uri == Uri("http://host1/test")
        }))
        .thenReturn(Success(HttpResponse(StatusCodes.BadGateway)))

      when(sendRequest
        .apply(argThat { (r: HttpRequest) =>
          r != null && r.uri == Uri("http://host2/test")
        }))
        .thenReturn(Success(HttpResponse().withEntity("ok")))

      client
        .sendAsync(ElasticRequest("GET", "/test"))
        .futureValue shouldBe ElasticResponse(
        200,
        Some(ElasticEntity.StringEntity("ok", None)),
        Map.empty)
    }

    "don't retry if no time left" in {

      val hosts = List(
        "host1",
        "host2"
      )

      val blacklist = mock[Blacklist]

      val (sendRequest, httpPool) = mockHttpPool()

      val client =
        new AkkaHttpClient(
          AkkaHttpClientSettings(hosts).copy(maxRetryTimeout = 0.seconds),
          blacklist,
          httpPool)

      when(blacklist.contains("host1")).thenReturn(false)
      when(blacklist.add("host1")).thenReturn(true)

      when(sendRequest
        .apply(argThat { (r: HttpRequest) =>
          r.uri == Uri("http://host1/test")
        }))
        .thenReturn(Success(HttpResponse(StatusCodes.BadGateway)))

      client
        .sendAsync(ElasticRequest("GET", "/test"))
        .futureValue shouldBe ElasticResponse(
        502,
        Some(ElasticEntity.StringEntity("", None)),
        Map.empty)
    }

    "blacklist on 502" in {

      val hosts = List(
        "host1",
        "host2"
      )

      val blacklist = mock[Blacklist]

      val (sendRequest, httpPool) = mockHttpPool()

      val client =
        new AkkaHttpClient(AkkaHttpClientSettings(hosts), blacklist, httpPool)

      when(blacklist.contains("host1")).thenReturn(false)
      when(blacklist.contains("host2")).thenReturn(false)
      when(blacklist.add("host1")).thenReturn(true)
      when(blacklist.remove("host2")).thenReturn(false)

      when(sendRequest
        .apply(argThat { (r: HttpRequest) =>
          r != null && r.uri == Uri("http://host1/test")
        }))
        .thenReturn(Success(HttpResponse(StatusCodes.BadGateway)))

      when(sendRequest
        .apply(argThat { (r: HttpRequest) =>
          r.uri == Uri("http://host2/test")
        }))
        .thenReturn(Success(HttpResponse().withEntity("host2")))

      client
        .sendAsync(ElasticRequest("GET", "/test"))
        .futureValue
    }

    "blacklist on exception" in {

      val hosts = List(
        "host1",
        "host2"
      )

      val blacklist = mock[Blacklist]

      val (sendRequest, httpPool) = mockHttpPool()

      val client =
        new AkkaHttpClient(AkkaHttpClientSettings(hosts), blacklist, httpPool)

      when(blacklist.contains("host1")).thenReturn(false)
      when(blacklist.contains("host2")).thenReturn(false)
      when(blacklist.add("host1")).thenReturn(true)
      when(blacklist.remove("host2")).thenReturn(false)

      when(sendRequest
        .apply(argThat { (r: HttpRequest) =>
          r != null && r.uri == Uri("http://host1/test")
        }))
        .thenReturn(Failure(new Exception("Some exception")))

      when(sendRequest
        .apply(argThat { (r: HttpRequest) =>
          r != null && r.uri == Uri("http://host2/test")
        }))
        .thenReturn(Success(HttpResponse().withEntity("host2")))

      client
        .sendAsync(ElasticRequest("GET", "/test"))
        .futureValue
    }

    "skip blacklisted hosts" in {

      val hosts = List(
        "host1",
        "host2"
      )

      val blacklist = mock[Blacklist]

      val (sendRequest, httpPool) = mockHttpPool()

      val client =
        new AkkaHttpClient(AkkaHttpClientSettings(hosts), blacklist, httpPool)

      when(blacklist.contains("host1")).thenReturn(true)
      when(blacklist.size).thenReturn(1)
      when(blacklist.contains("host2")).thenReturn(false)
      when(blacklist.remove("host2")).thenReturn(false)

      when(sendRequest
        .apply(argThat { (r: HttpRequest) =>
          r.uri == Uri("http://host2/test")
        }))
        .thenReturn(Success(HttpResponse().withEntity("host2")))

      client
        .sendAsync(ElasticRequest("GET", "/test"))
        .futureValue shouldBe ElasticResponse(
        200,
        Some(ElasticEntity.StringEntity("host2", None)),
        Map.empty)
    }

  }
}

package com.sksamuel.elastic4s.akka

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import com.sksamuel.elastic4s.ElasticRequest
import com.sksamuel.elastic4s.akka.AkkaHttpClient.AllHostsBlacklistedException
import com.sksamuel.elastic4s.{HttpEntity => ElasticEntity, HttpResponse => ElasticResponse}
import org.scalamock.function.MockFunction1
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class AkkaHttpClientMockTest
  extends WordSpec
    with Matchers
    with MockFactory
    with ScalaFutures
    with IntegrationPatience
    with BeforeAndAfterAll {

  private implicit lazy val system: ActorSystem = ActorSystem()

  override def afterAll: Unit = {
    system.terminate()
  }

  def mockHttpPool(): (MockFunction1[HttpRequest, Try[HttpResponse]], TestHttpPoolFactory) = {
    val sendRequest = mockFunction[HttpRequest, Try[HttpResponse]]
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

      (blacklist.contains _).expects("host1").returns(false)
      (blacklist.contains _).expects("host2").returns(false)
      (blacklist.add _).expects("host1").returns(true)
      (blacklist.remove _).expects("host2").returns(false)

      sendRequest
        .expects(argThat { r: HttpRequest =>
          r.uri == Uri("http://host1/test")
        })
        .returns(Success(HttpResponse(StatusCodes.BadGateway)))

      sendRequest
        .expects(argThat { r: HttpRequest =>
          r.uri == Uri("http://host2/test")
        })
        .returns(Success(HttpResponse().withEntity("ok")))

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

      (blacklist.contains _).expects("host1").returns(false)
      (blacklist.add _).expects("host1").returns(true)

      sendRequest
        .expects(argThat { r: HttpRequest =>
          r.uri == Uri("http://host1/test")
        })
        .returns(Success(HttpResponse(StatusCodes.BadGateway)))

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

      (blacklist.contains _).expects("host1").returns(false)
      (blacklist.contains _).expects("host2").returns(false)
      (blacklist.add _).expects("host1").returns(true)
      (blacklist.remove _).expects("host2").returns(false)

      sendRequest
        .expects(argThat { r: HttpRequest =>
          r.uri == Uri("http://host1/test")
        })
        .returns(Success(HttpResponse(StatusCodes.BadGateway)))

      sendRequest
        .expects(argThat { r: HttpRequest =>
          r.uri == Uri("http://host2/test")
        })
        .returns(Success(HttpResponse().withEntity("host2")))

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

      (blacklist.contains _).expects("host1").returns(false)
      (blacklist.contains _).expects("host2").returns(false)
      (blacklist.add _).expects("host1").returns(true)
      (blacklist.remove _).expects("host2").returns(false)

      sendRequest
        .expects(argThat { r: HttpRequest =>
          r.uri == Uri("http://host1/test")
        })
        .returns(Failure(new Exception("Some exception")))

      sendRequest
        .expects(argThat { r: HttpRequest =>
          r.uri == Uri("http://host2/test")
        })
        .returns(Success(HttpResponse().withEntity("host2")))

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

      (blacklist.contains _).expects("host1").returns(true)
      (blacklist.size _).expects().returns(1)
      (blacklist.contains _).expects("host2").returns(false)
      (blacklist.remove _).expects("host2").returns(false)

      sendRequest
        .expects(argThat { r: HttpRequest =>
          r.uri == Uri("http://host2/test")
        })
        .returns(Success(HttpResponse().withEntity("host2")))

      client
        .sendAsync(ElasticRequest("GET", "/test"))
        .futureValue shouldBe ElasticResponse(
        200,
        Some(ElasticEntity.StringEntity("host2", None)),
        Map.empty)
    }

    "return error if all hosts are blacklisted" in {

      val hosts = List(
        "host1",
        "host2"
      )

      val blacklist = mock[Blacklist]

      val (_, httpPool) = mockHttpPool()

      val client =
        new AkkaHttpClient(AkkaHttpClientSettings(hosts), blacklist, httpPool)

      (blacklist.contains _).expects("host1").returns(true)
      (blacklist.size _).expects().returns(2)

      client
        .sendAsync(ElasticRequest("GET", "/test"))
        .failed
        .futureValue shouldBe AllHostsBlacklistedException
    }

  }
}

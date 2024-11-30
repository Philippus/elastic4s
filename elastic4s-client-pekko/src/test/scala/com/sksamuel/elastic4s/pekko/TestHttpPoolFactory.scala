package com.sksamuel.elastic4s.pekko

import org.apache.pekko.NotUsed
import org.apache.pekko.http.scaladsl.model.{HttpRequest, HttpResponse}
import org.apache.pekko.stream.scaladsl.Flow

import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.Try

class TestHttpPoolFactory(sendRequest: HttpRequest => Try[HttpResponse], timeout: FiniteDuration = 2.seconds)
    extends HttpPoolFactory {

  override def create[T](): Flow[(HttpRequest, T), (HttpRequest, Try[HttpResponse], T), NotUsed] = {
    Flow[(HttpRequest, T)]
      .map {
        case (r, s) => (r, Try(sendRequest(r)).flatten, s)
      }
  }

  override def shutdown(): Future[Unit] = Future.successful(())
}

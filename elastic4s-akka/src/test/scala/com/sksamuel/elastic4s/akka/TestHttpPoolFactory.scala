package com.sksamuel.elastic4s.akka

import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.Try

import akka.NotUsed
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.Flow

class TestHttpPoolFactory(sendRequest: HttpRequest => Try[HttpResponse],
                          timeout: FiniteDuration = 2.seconds) extends HttpPoolFactory {

  override def create[T](): Flow[(HttpRequest, T), (Try[HttpResponse], T), NotUsed] = {
    Flow[(HttpRequest, T)]
      .map {
        case (r, s) => (Try(sendRequest(r)).flatten, s)
      }
  }

  override def shutdown(): Future[Unit] = Future.successful(())
}

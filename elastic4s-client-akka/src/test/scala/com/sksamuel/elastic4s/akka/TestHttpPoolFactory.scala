package com.sksamuel.elastic4s.akka

import akka.NotUsed
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.Flow

import scala.concurrent.Future
import scala.util.Try

class TestHttpPoolFactory(sendRequest: HttpRequest => Try[HttpResponse])
    extends HttpPoolFactory {

  override def create[T](): Flow[(HttpRequest, T), (HttpRequest, Try[HttpResponse], T), NotUsed] = {
    Flow[(HttpRequest, T)]
      .map {
        case (r, s) => (r, Try(sendRequest(r)).flatten, s)
      }
  }

  override def shutdown(): Future[Unit] = Future.successful(())
}

package com.sksamuel.elastic4s.akka

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Try

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.stream.scaladsl.Flow

private[akka] class DefaultHttpPoolFactory(settings: ConnectionPoolSettings)(
  implicit system: ActorSystem)
  extends HttpPoolFactory {

  private val http = Http()

  private val poolSettings = settings.withResponseEntitySubscriptionTimeout(
    Duration.Inf) // we guarantee to consume consume data from all responses

  override def create[T]()
  : Flow[(HttpRequest, T), (Try[HttpResponse], T), NotUsed] = {
    http.superPool[T](
      settings = poolSettings
    )
  }

  override def shutdown(): Future[Unit] = http.shutdownAllConnectionPools()
}

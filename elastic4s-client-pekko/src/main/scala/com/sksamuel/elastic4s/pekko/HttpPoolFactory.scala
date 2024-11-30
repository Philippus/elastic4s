package com.sksamuel.elastic4s.pekko

import org.apache.pekko.NotUsed
import org.apache.pekko.http.scaladsl.model.{HttpRequest, HttpResponse}
import org.apache.pekko.stream.scaladsl.Flow

import scala.concurrent.Future
import scala.util.Try

/** Factory for Pekko's http pool flow.
  */
private[pekko] trait HttpPoolFactory {

  def create[T](): Flow[(HttpRequest, T), (HttpRequest, Try[HttpResponse], T), NotUsed]

  def shutdown(): Future[Unit]
}

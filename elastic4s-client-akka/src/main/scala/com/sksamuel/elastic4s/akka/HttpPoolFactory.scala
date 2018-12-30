package com.sksamuel.elastic4s.akka

import scala.concurrent.Future
import scala.util.Try

import akka.NotUsed
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.Flow

/**
  * Factory for Akka's http pool flow.
  */
private[akka] trait HttpPoolFactory {

  def create[T](): Flow[(HttpRequest, T), (Try[HttpResponse], T), NotUsed]

  def shutdown(): Future[Unit]
}

package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.ext.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class SearchScrollRequest(id: String, keepAlive: Option[String] = None) {

  def keepAlive(keepAlive: String): SearchScrollRequest        = copy(keepAlive = keepAlive.some)
  def keepAlive(duration: FiniteDuration): SearchScrollRequest = copy(keepAlive = s"${duration.toSeconds}s".some)
}

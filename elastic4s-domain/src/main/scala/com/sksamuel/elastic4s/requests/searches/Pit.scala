package com.sksamuel.elastic4s.requests.searches

import scala.concurrent.duration.FiniteDuration

case class Pit(id: String, keepAlive: Option[FiniteDuration] = None) {
  def keepAlive(keepAlive: FiniteDuration): Pit = copy(keepAlive = Some(keepAlive))
}

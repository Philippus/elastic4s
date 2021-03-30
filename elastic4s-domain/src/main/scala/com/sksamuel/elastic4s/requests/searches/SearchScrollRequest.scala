package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

import scala.concurrent.duration.FiniteDuration

case class SearchScrollRequest(id: String, keepAlive: Option[String] = None) {

  def keepAlive(keepAlive: String): SearchScrollRequest = copy(keepAlive = keepAlive.some)
  def keepAlive(duration: FiniteDuration): SearchScrollRequest = copy(keepAlive = Some(duration.toSeconds + "s"))
}

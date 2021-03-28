package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

trait ScrollApi {

  def searchScroll(id: String, keepAlive: String): SearchScrollRequest = SearchScrollRequest(id).keepAlive(keepAlive)
  def searchScroll(id: String): SearchScrollRequest = SearchScrollRequest(id)

  def clearScroll(first: String, rest: String*): ClearScrollRequest = clearScroll(first +: rest)
  def clearScroll(ids: Iterable[String]): ClearScrollRequest        = ClearScrollRequest(ids.toSeq)
}





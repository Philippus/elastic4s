package com.sksamuel.elastic4s.searches

import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

trait ScrollApi {

  def searchScroll(id: String, keepAlive: String): SearchScrollDefinition =
    SearchScrollDefinition(id).keepAlive(keepAlive)
  def searchScroll(id: String): SearchScrollDefinition = SearchScrollDefinition(id)

  def clearScroll(first: String, rest: String*): ClearScrollDefinition = clearScroll(first +: rest)
  def clearScroll(ids: Iterable[String]): ClearScrollDefinition        = ClearScrollDefinition(ids.toSeq)
}

case class SearchScrollDefinition(id: String, keepAlive: Option[String] = None) {

  def keepAlive(keepAlive: String): SearchScrollDefinition        = copy(keepAlive = keepAlive.some)
  def keepAlive(duration: FiniteDuration): SearchScrollDefinition = copy(keepAlive = Some(duration.toSeconds + "s"))
}

case class ClearScrollDefinition(ids: Seq[String])

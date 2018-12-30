package com.sksamuel.elastic4s.requests.get

import com.sksamuel.exts.OptionImplicits._

case class MultiGetRequest(gets: Seq[GetRequest],
                           preference: Option[String] = None,
                           realtime: Option[Boolean] = None,
                           refresh: Option[Boolean] = None) {

  def realtime(realtime: Boolean): MultiGetRequest    = copy(realtime = realtime.some)
  def refresh(refresh: Boolean): MultiGetRequest      = copy(refresh = refresh.some)
  def preference(preference: String): MultiGetRequest = copy(preference = preference.some)
}

package com.sksamuel.elastic4s.get

import com.sksamuel.exts.OptionImplicits._

case class MultiGetDefinition(gets: Seq[GetDefinition],
                              preference: Option[String] = None,
                              realtime: Option[Boolean] = None,
                              refresh: Option[Boolean] = None) {

  def realtime(realtime: Boolean): MultiGetDefinition    = copy(realtime = realtime.some)
  def refresh(refresh: Boolean): MultiGetDefinition      = copy(refresh = refresh.some)
  def preference(preference: String): MultiGetDefinition = copy(preference = preference.some)
}

package com.sksamuel.elastic4s.get

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.get.{MultiGetRequest, MultiGetRequestBuilder}
import org.elasticsearch.cluster.routing.Preference

case class MultiGetDefinition(gets: Seq[GetDefinition],
                              preference: Option[String] = None,
                              realtime: Option[Boolean] = None,
                              refresh: Option[Boolean] = None) {

  def populate(builder: MultiGetRequestBuilder): Unit = {
    preference.foreach(builder.setPreference)
    realtime.foreach(builder.setRealtime)
    refresh.foreach(builder.setRefresh)
    gets foreach { get =>
      val item = new MultiGetRequest.Item(get.indexAndType.index, get.indexAndType.`type`, get.id)
      item.fetchSourceContext(get.build.fetchSourceContext)
      item.routing(get.build.routing)
      item.storedFields(get.build.storedFields: _*)
      item.version(get.build.version)
      builder.add(item)
    }
  }

  def realtime(realtime: Boolean): MultiGetDefinition = copy(realtime = realtime.some)
  def refresh(refresh: Boolean): MultiGetDefinition = copy(refresh = refresh.some)
  def preference(preference: String): MultiGetDefinition = copy(preference = preference.some)
  def preference(preference: Preference): MultiGetDefinition = copy(preference = preference.`type`.some)
}

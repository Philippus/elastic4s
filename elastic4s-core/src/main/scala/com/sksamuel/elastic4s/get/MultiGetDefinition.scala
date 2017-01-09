package com.sksamuel.elastic4s.get

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.get.{MultiGetRequest, MultiGetRequestBuilder}
import org.elasticsearch.cluster.routing.Preference
import org.elasticsearch.index.VersionType
import org.elasticsearch.search.fetch.subphase.FetchSourceContext

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

      get.fetchSource.foreach { context =>
        item.fetchSourceContext(new FetchSourceContext(context.enabled, context.includes.toArray, context.excludes.toArray))
      }

      get.routing.foreach(item.routing)
      get.version.foreach(item.version)
      get.versionType.map(VersionType.fromString).foreach(item.versionType)
      get.parent.foreach(item.parent)

      if (get.storedFields.nonEmpty)
        item.storedFields(get.storedFields: _*)

      builder.add(item)
    }
  }

  def realtime(realtime: Boolean): MultiGetDefinition = copy(realtime = realtime.some)
  def refresh(refresh: Boolean): MultiGetDefinition = copy(refresh = refresh.some)
  def preference(preference: String): MultiGetDefinition = copy(preference = preference.some)
  def preference(preference: Preference): MultiGetDefinition = copy(preference = preference.`type`.some)
}

package com.sksamuel.elastic4s2.get

import com.sksamuel.elastic4s2.ProxyClients
import org.elasticsearch.action.get.{MultiGetAction, MultiGetRequest, MultiGetRequestBuilder}
import org.elasticsearch.cluster.routing.Preference

case class MultiGetDefinition(gets: Seq[GetDefinition]) {

  val _builder = new MultiGetRequestBuilder(ProxyClients.client, MultiGetAction.INSTANCE)

  gets foreach { get =>
    val item = new MultiGetRequest.Item(get.indexAndType.index, get.indexAndType.`type`, get.id)
    item.fetchSourceContext(get.build.fetchSourceContext)
    item.routing(get.build.routing)
    item.storedFields(get.build.storedFields: _*)
    item.version(get.build.version)
    _builder.add(item)
  }

  def build: MultiGetRequest = _builder.request()

  def realtime(realtime: Boolean): this.type = {
    _builder.setRealtime(realtime)
    this
  }

  def realtime(preference: Preference): this.type = {
    _builder.setPreference(preference.`type`())
    this
  }

  def realtime(preference: String): this.type = {
    _builder.setPreference(preference)
    this
  }

  def refresh(refresh: Boolean): this.type = {
    _builder.setRefresh(refresh)
    this
  }
}

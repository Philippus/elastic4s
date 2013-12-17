package com.sksamuel.elastic4s

import org.elasticsearch.action.get.{MultiGetRequest, MultiGetAction, MultiGetRequestBuilder}

/** @author Stephen Samuel */
trait MultiGetDsl extends GetDsl {
  def multiget(gets: GetDefinition*) = new MultiGetDefinition(gets)
}

class MultiGetDefinition(gets: Iterable[GetDefinition]) extends RequestDefinition(MultiGetAction.INSTANCE) {

  private val _builder = new MultiGetRequestBuilder(null)
  gets.foreach(get => _builder.add(get.index, get.`type`, get.id))
  def build: MultiGetRequest = _builder.request()

  def realtime(r: Boolean): MultiGetDefinition = {
    _builder.setRealtime(r)
    this
  }

  def refresh(r: Boolean): MultiGetDefinition = {
    _builder.setRefresh(r)
    this
  }

  def preference(pref: Preference): MultiGetDefinition = preference(pref.elastic)
  def preference(pref: String): MultiGetDefinition = {
    _builder.setPreference(pref)
    this
  }
}
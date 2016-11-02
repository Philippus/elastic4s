package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.ProxyClients
import org.elasticsearch.action.search.{MultiSearchAction, MultiSearchRequest, MultiSearchRequestBuilder}

case class MultiSearchDefinition(searches: Iterable[SearchDefinition]) {
  def build: MultiSearchRequest = {
    val builder = new MultiSearchRequestBuilder(ProxyClients.client, MultiSearchAction.INSTANCE)
    searches foreach (builder add _.build)
    builder.request()
  }
}

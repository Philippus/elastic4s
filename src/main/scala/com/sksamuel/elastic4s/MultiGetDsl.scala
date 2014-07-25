package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes.{ DefinitionAttributePreference, DefinitionAttributeRefresh }
import org.elasticsearch.action.get.{ MultiGetRequest, MultiGetRequestBuilder }

/** @author Stephen Samuel */
trait MultiGetDsl extends GetDsl {
  def multiget(gets: GetDefinition*) = new MultiGetDefinition(gets)
}

class MultiGetDefinition(gets: Iterable[GetDefinition])
    extends DefinitionAttributePreference
    with DefinitionAttributeRefresh {

  val _builder = new MultiGetRequestBuilder(ProxyClients.client)
  gets.foreach(get => _builder.add(get.indexesTypes.index, get.indexesTypes.typ.orNull, get.id))
  def build: MultiGetRequest = _builder.request()

  def realtime(realtime: Boolean): this.type = {
    _builder.setRealtime(realtime)
    this
  }
}

package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributePreference, DefinitionAttributeRefresh}
import org.elasticsearch.action.get.{MultiGetRequest, MultiGetRequestBuilder, MultiGetResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

/** @author Stephen Samuel */
trait MultiGetDsl extends GetDsl {

  implicit object MultiGetDefinitionExecutable extends Executable[MultiGetDefinition, MultiGetResponse] {
    override def apply(c: Client, t: MultiGetDefinition): Future[MultiGetResponse] = {
      injectFuture(c.multiGet(t.build, _))
    }
  }
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

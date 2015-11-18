package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributePreference, DefinitionAttributeRefresh}
import org.elasticsearch.action.get.MultiGetRequest.Item
import org.elasticsearch.action.get.{MultiGetAction, MultiGetRequest, MultiGetRequestBuilder, MultiGetResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

/** @author Stephen Samuel */
trait MultiGetDsl extends GetDsl {

  implicit object MultiGetDefinitionExecutable
    extends Executable[MultiGetDefinition, MultiGetResponse, MultiGetResponse] {
    override def apply(c: Client, t: MultiGetDefinition): Future[MultiGetResponse] = {
      injectFuture(c.multiGet(t.build, _))
    }
  }
}

class MultiGetDefinition(gets: Iterable[GetDefinition])
  extends DefinitionAttributePreference
    with DefinitionAttributeRefresh {

  val _builder = new MultiGetRequestBuilder(ProxyClients.client, MultiGetAction.INSTANCE)

  gets foreach { get =>
    val item = new Item(get.indexTypes.index, get.indexTypes.types.headOption.orNull, get.id)
    item.routing(get.build.routing())
    item.fields(get.build.fields(): _*)
    item.version(get.build.version())
    _builder.add(item)
  }

  def build: MultiGetRequest = _builder.request()

  def realtime(realtime: Boolean): this.type = {
    _builder.setRealtime(realtime)
    this
  }
}

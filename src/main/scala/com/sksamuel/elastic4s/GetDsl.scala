package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests
import org.elasticsearch.action.get._

/** @author Stephen Samuel */
trait GetDsl extends IndexesTypesDsl {

  def get = new GetExpectsId
  def get(id: Any) = new GetWithIdExpectsFrom(id.toString)
  implicit def any2get(id: Any) = new GetWithIdExpectsFrom(id.toString)
  class GetExpectsId {
    def id(id: Any) = new GetWithIdExpectsFrom(id.toString)
  }
  class GetWithIdExpectsFrom(id: String) {
    def from(index: IndexesTypes): GetDefinition = new GetDefinition(index, id)
    def from(index: String, `type`: String): GetDefinition = from(IndexesTypes(index, `type`))
  }
}

case class GetDefinition(indexesTypes: IndexesTypes, id: String) extends RequestDefinition(GetAction.INSTANCE) {

  private val _builder = Requests.getRequest(indexesTypes.index).`type`(indexesTypes.typ.orNull).id(id)
  def build = _builder

  def routing(r: String) = {
    _builder.routing(r)
    this
  }

  def realtime(r: Boolean) = {
    _builder.realtime(r)
    this
  }

  def preference(pref: Preference): GetDefinition = preference(pref.elastic)
  def preference(pref: String): GetDefinition = {
    _builder.preference(pref)
    this
  }
}

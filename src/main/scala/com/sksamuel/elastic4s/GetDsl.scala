package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests
import org.elasticsearch.action.get._
import org.elasticsearch.search.fetch.source.FetchSourceContext
import org.elasticsearch.index.VersionType

/** @author Stephen Samuel */
trait GetDsl {

  def get = new GetExpectsId
  def get(id: Any) = new GetWithIdExpectsFrom(id.toString)
  implicit def any2get(id: Any) = new GetWithIdExpectsFrom(id.toString)
  class GetExpectsId {
    def id(id: Any) = new GetWithIdExpectsFrom(id.toString)
  }
  class GetWithIdExpectsFrom(id: String) {
    def from(kv: (String, String)): GetDefinition = from(kv._1, kv._2)
    def from(index: String): GetDefinition = {
      val tokens = index.split("/")
      from(tokens(0), tokens(1))
    }
    def from(index: String, `type`: String): GetDefinition = new GetDefinition(index, `type`, id)
  }
}

case class GetDefinition(index: String, `type`: String, id: String) extends RequestDefinition(GetAction.INSTANCE) {

  private val _builder = Requests.getRequest(index).`type`(`type`).id(id)
  def build = _builder

  def routing(r: String) = {
    _builder.routing(r)
    this
  }

  def fetchSourceContext(r: FetchSourceContext) = {
    _builder.fetchSourceContext(r)
    this
  }

  def realtime(r: Boolean) = {
    _builder.realtime(r)
    this
  }

  def version(v: Long) = {
    _builder.version(v)
    this
  }

  def versionType(v: VersionType) = {
    _builder.versionType(v)
    this
  }

  def preference(pref: Preference): GetDefinition = preference(pref.elastic)
  def preference(pref: String): GetDefinition = {
    _builder.preference(pref)
    this
  }
}

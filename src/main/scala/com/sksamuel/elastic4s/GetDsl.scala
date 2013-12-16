package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests
import org.elasticsearch.action.get._

/** @author Stephen Samuel */
trait GetDsl {

  def get = new GetExpectsId
  implicit def any2get(id: Any) = new GetWithIdExpectsFrom(id.toString)
  class GetExpectsId {
    def id(id: Any) = new GetWithIdExpectsFrom(id.toString)
  }
  class GetWithIdExpectsFrom(id: String) {
    def from(kv: (String, String)): GetDefinition = from(kv._1, kv._2)
    def from(index: String): GetDefinition = from(index.split("/"))
    private def from(seq: Seq[String]): GetDefinition = from(seq(0), seq(1))
    def from(index: String, `type`: String): GetDefinition = new GetDefinition(index, `type`, id)
  }

  case class GetDefinition(index: String, `type`: String, id: String) extends RequestDefinition(GetAction.INSTANCE) {

    private val _builder = Requests.getRequest(index).`type`(`type`).id(id)
    def build = _builder

    def routing(r: String) = {
      _builder.routing(r)
      this
    }

    def preference(pref: String) = {
      _builder.preference(pref)
      this
    }

    def preference(pref: Preference) = {
      _builder.preference(pref.elastic)
      this
    }
  }

  class MultiGetDefinition(gets: Iterable[GetDefinition]) extends RequestDefinition(MultiGetAction.INSTANCE) {
    def build: MultiGetRequest = {
      val builder = new MultiGetRequestBuilder(null)
      gets.foreach(get => builder.add(get.index, get.`type`, get.id))
      builder.request()
    }
  }
}

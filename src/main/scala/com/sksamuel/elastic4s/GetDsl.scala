package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests
import org.elasticsearch.index.VersionType
import org.elasticsearch.search.fetch.source.FetchSourceContext

/** @author Stephen Samuel */
trait GetDsl extends IndexesTypesDsl {

  case object get {
    def id(id: Any) = new GetWithIdExpectsFrom(id.toString)
  }
  def get(id: Any) = new GetWithIdExpectsFrom(id.toString)
  implicit def any2get(id: Any): GetWithIdExpectsFrom = new GetWithIdExpectsFrom(id.toString)

  class GetWithIdExpectsFrom(id: String) {
    def from(index: IndexesTypes): GetDefinition = new GetDefinition(index, id)
    def from(index: String, `type`: String): GetDefinition = from(IndexesTypes(index, `type`))
  }
}

case class GetDefinition(indexesTypes: IndexesTypes, id: String) {

  private val _builder = Requests.getRequest(indexesTypes.index).`type`(indexesTypes.typ.orNull).id(id)
  def build = _builder

  def fetchSourceContext(context: Boolean) = {
    _builder.fetchSourceContext(new FetchSourceContext(context))
    this
  }

  def fetchSourceContext(context: FetchSourceContext) = {
    _builder.fetchSourceContext(context)
    this
  }

  def fields(fields: String*) = {
    _builder.fields(fields: _*)
    this
  }

  def parent(p: String) = {
    _builder.parent(p)
    this
  }

  def ignoreErrorsOnGeneratedFields(ignoreErrorsOnGeneratedFields: Boolean) = {
    _builder.ignoreErrorsOnGeneratedFields(ignoreErrorsOnGeneratedFields)
    this
  }

  def version(version: Long) = {
    _builder.version(version)
    this
  }

  def versionType(versionType: VersionType) = {
    _builder.versionType(versionType)
    this
  }

  def preference(pref: Preference): GetDefinition = preference(pref.elastic)
  def preference(pref: String): GetDefinition = {
    _builder.preference(pref)
    this
  }

  def realtime(r: Boolean) = {
    _builder.realtime(r)
    this
  }

  def refresh(refresh: Boolean) = {
    _builder.refresh(refresh)
    this
  }

  def routing(r: String) = {
    _builder.routing(r)
    this
  }


}

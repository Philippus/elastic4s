package com.sksamuel.elastic4s2.get

import com.sksamuel.elastic4s2.IndexAndType
import org.elasticsearch.client.Requests
import org.elasticsearch.cluster.routing.Preference
import org.elasticsearch.index.VersionType
import org.elasticsearch.search.fetch.subphase.FetchSourceContext

case class GetDefinition(indexAndType: IndexAndType, id: String) {
  require(id.toString.nonEmpty, "id must not be null or empty")

  private val _builder = Requests.getRequest(indexAndType.index).`type`(indexAndType.`type`).id(id)
  def build = _builder

  def fetchSourceContext(context: Boolean) = {
    _builder.fetchSourceContext(new FetchSourceContext(context))
    this
  }

  def fetchSourceContext(include: Iterable[String], exclude: Iterable[String] = Nil) = {
    _builder.fetchSourceContext(new FetchSourceContext(include.toArray, exclude.toArray))
    this
  }

  def fetchSourceContext(context: FetchSourceContext) = {
    _builder.fetchSourceContext(context)
    this
  }

  def fields(fs: String*): GetDefinition = fields(fs)
  def fields(fs: Iterable[String]): GetDefinition = {
    _builder.storedFields(fs.toSeq: _*)
    this
  }

  def parent(p: String) = {
    _builder.parent(p)
    this
  }

  def preference(pref: Preference): GetDefinition = preference(pref.`type`())

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

  def version(version: Long) = {
    _builder.version(version)
    this
  }

  def versionType(versionType: VersionType) = {
    _builder.versionType(versionType)
    this
  }
}

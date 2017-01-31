package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.IndexAndType
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.fetch.subphase.FetchSourceContext

case class GetDefinition(indexAndType: IndexAndType,
                         id: String,
                         storedFields: Seq[String] = Nil,
                         parent: Option[String] = None,
                         preference: Option[String] = None,
                         realtime: Option[Boolean] = None,
                         refresh: Option[Boolean] = None,
                         routing: Option[String] = None,
                         version: Option[Long] = None,
                         versionType: Option[String] = None,
                         fetchSource: Option[FetchSourceContext] = None) {
  require(indexAndType != null, "indexAndType must not be null")
  require(id.toString.nonEmpty, "id must not be null or empty")

  def fetchSourceContext(sourceEnabled: Boolean): GetDefinition =
    copy(fetchSource = new FetchSourceContext(sourceEnabled).some)

  def fetchSourceContext(include: Iterable[String],
                         exclude: Iterable[String] = Nil): GetDefinition =
    copy(fetchSource = new FetchSourceContext(true, include.toArray, exclude.toArray).some)

  def fetchSourceContext(context: FetchSourceContext): GetDefinition = copy(fetchSource = context.some)

  @deprecated("use storedFields", "5.0.0")
  def fields(fs: String*): GetDefinition = storedFields(fs)

  @deprecated("use storedFields", "5.0.0")
  def fields(fs: Iterable[String]): GetDefinition = storedFields(fs)

  def storedFields(first: String, rest: String*): GetDefinition = storedFields(first +: rest)
  def storedFields(fs: Iterable[String]): GetDefinition = copy(storedFields = fs.toSeq)

  def parent(p: String): GetDefinition = copy(parent = p.some)

  def preference(pref: com.sksamuel.elastic4s.Preference): GetDefinition = preference(pref.value)
  def preference(pref: String): GetDefinition = copy(preference = pref.some)

  def realtime(r: Boolean): GetDefinition = copy(realtime = r.some)
  def refresh(r: Boolean): GetDefinition = copy(refresh = r.some)
  def routing(r: String): GetDefinition = copy(routing = r.some)
  def version(ver: Long): GetDefinition = copy(version = ver.some)

  def versionType(versionType: String): GetDefinition = copy(versionType = versionType.some)
}

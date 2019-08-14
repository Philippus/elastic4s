package com.sksamuel.elastic4s.requests.get

import com.sksamuel.elastic4s.IndexLike
import com.sksamuel.elastic4s.requests.common.{FetchSourceContext, Preference, VersionType}
import com.sksamuel.exts.OptionImplicits._

case class GetRequest(index: IndexLike,
                      id: String,
                      storedFields: Seq[String] = Nil,
                      parent: Option[String] = None,
                      preference: Option[String] = None,
                      realtime: Option[Boolean] = None,
                      refresh: Option[Boolean] = None,
                      routing: Option[String] = None,
                      version: Option[Long] = None,
                      versionType: Option[VersionType] = None,
                      fetchSource: Option[FetchSourceContext] = None) {
  require(index != null, "indexAndTypes must not be null")
  require(id.toString.nonEmpty, "id must not be null or empty")

  def fetchSourceContext(fetch: Boolean): GetRequest = copy(fetchSource = FetchSourceContext(fetch).some)

  def fetchSourceContext(include: Iterable[String], exclude: Iterable[String] = Nil): GetRequest =
    copy(fetchSource = FetchSourceContext(fetchSource = true, include.toArray, exclude.toArray).some)

  def fetchSourceContext(context: FetchSourceContext): GetRequest = copy(fetchSource = context.some)

  def fetchSourceInclude(include: String): GetRequest            = fetchSourceContext(List(include), Nil)
  def fetchSourceInclude(includes: Iterable[String]): GetRequest = fetchSourceContext(includes, Nil)
  def fetchSourceInclude(includes: String*): GetRequest          = fetchSourceContext(includes, Nil)

  def fetchSourceExclude(exclude: String): GetRequest            = fetchSourceContext(Nil, List(exclude))
  def fetchSourceExclude(excludes: Iterable[String]): GetRequest = fetchSourceContext(Nil, excludes)
  def fetchSourceExclude(excludes: String*): GetRequest          = fetchSourceContext(Nil, excludes)

  def storedFields(first: String, rest: String*): GetRequest = storedFields(first +: rest)
  def storedFields(fs: Iterable[String]): GetRequest         = copy(storedFields = fs.toSeq)

  def parent(p: String): GetRequest = copy(parent = p.some)

  def preference(pref: Preference): GetRequest = preference(pref.value)
  def preference(pref: String): GetRequest                            = copy(preference = pref.some)

  def realtime(r: Boolean): GetRequest = copy(realtime = r.some)
  def refresh(r: Boolean): GetRequest  = copy(refresh = r.some)
  def routing(r: String): GetRequest   = copy(routing = r.some)
  def version(ver: Long): GetRequest   = copy(version = ver.some)

  def versionType(versionType: String): GetRequest      = this.versionType(VersionType.valueOf(versionType))
  def versionType(versionType: VersionType): GetRequest = copy(versionType = versionType.some)
}

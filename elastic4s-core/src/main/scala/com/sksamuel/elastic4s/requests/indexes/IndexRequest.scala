package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.requests.bulk.BulkCompatibleRequest
import com.sksamuel.elastic4s.requests.common.{RefreshPolicy, VersionType}
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration

case class IndexRequest(indexAndType: IndexAndType,
                        id: Option[String] = None,
                        createOnly: Option[Boolean] = None,
                        refresh: Option[RefreshPolicy] = None,
                        parent: Option[String] = None,
                        pipeline: Option[String] = None,
                        routing: Option[String] = None,
                        timeout: Option[String] = None,
                        version: Option[Long] = None,
                        versionType: Option[VersionType] = None,
                        fields: Seq[FieldValue] = Nil,
                        source: Option[String] = None)
    extends BulkCompatibleRequest {
  require(indexAndType != null, "index must not be null or empty")

  def doc(json: String): IndexRequest       = source(json)
  def doc[T: Indexable](t: T): IndexRequest = source(t)

  def source(json: String): IndexRequest                              = copy(source = json.some)
  def source[T](t: T)(implicit indexable: Indexable[T]): IndexRequest = copy(source = indexable.json(t).some)

  def id(id: String): IndexRequest     = withId(id)
  def withId(id: String): IndexRequest = copy(id = id.some)

  def pipeline(pipeline: String): IndexRequest = copy(pipeline = pipeline.some)
  def parent(parent: String): IndexRequest     = copy(parent = parent.some)

  @deprecated("use the typed version, refresh(RefreshPolicy)", "6.0.0")
  def refresh(refresh: String): IndexRequest        = copy(refresh = RefreshPolicy.valueOf(refresh).some)
  def refresh(refresh: RefreshPolicy): IndexRequest = copy(refresh = refresh.some)

  def refreshImmediately: IndexRequest = refresh(RefreshPolicy.IMMEDIATE)

  def routing(routing: String): IndexRequest = copy(routing = routing.some)

  def version(version: Long): IndexRequest                = copy(version = version.some)
  def versionType(versionType: VersionType): IndexRequest = copy(versionType = versionType.some)

  def timeout(timeout: String): IndexRequest          = copy(timeout = timeout.some)
  def timeout(duration: FiniteDuration): IndexRequest = copy(timeout = (duration.toSeconds + "s").some)

  // if set to true then trying to update a document will fail
  def createOnly(createOnly: Boolean): IndexRequest = copy(createOnly = createOnly.some)

  def fields(_fields: (String, Any)*): IndexRequest          = fields(_fields.toMap)
  def fields(_fields: Iterable[(String, Any)]): IndexRequest = fields(_fields.toMap)
  def fields(fields: Map[String, Any]): IndexRequest         = copy(fields = FieldsMapper.mapFields(fields))
  def fieldValues(fields: FieldValue*): IndexRequest         = copy(fields = fields)
}

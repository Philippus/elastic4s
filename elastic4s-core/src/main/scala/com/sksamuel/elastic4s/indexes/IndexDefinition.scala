package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.bulk.BulkCompatibleDefinition
import com.sksamuel.elastic4s.{FieldValue, FieldsMapper, IndexAndType, Indexable}
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.index.VersionType

case class IndexDefinition(indexAndType: IndexAndType,
                           id: Option[Any] = None,
                           createOnly: Option[Boolean] = None,
                           refresh: Option[RefreshPolicy] = None,
                           parent: Option[String] = None,
                           pipeline: Option[String] = None,
                           routing: Option[String] = None,
                           timestamp: Option[String] = None,
                           timeout: Option[String] = None,
                           version: Option[Long] = None,
                           versionType: Option[VersionType] = None,
                           fields: Seq[FieldValue] = Nil,
                           source: Option[String] = None) extends BulkCompatibleDefinition {
  require(indexAndType != null, "index must not be null or empty")

  def doc(json: String): IndexDefinition = source(json)
  def doc[T: Indexable](t: T): IndexDefinition = source(t)

  def source(json: String): IndexDefinition = copy(source = json.some)
  def source[T](t: T)(implicit indexable: Indexable[T]): IndexDefinition = copy(source = indexable.json(t).some)

  def id(id: Any): IndexDefinition = withId(id)
  def withId(id: Any): IndexDefinition = copy(id = id.some)

  def pipeline(pipeline: String): IndexDefinition = copy(pipeline = pipeline.some)
  def parent(parent: String): IndexDefinition = copy(parent = parent.some)
  def refresh(refresh: String): IndexDefinition = copy(refresh = RefreshPolicy.valueOf(refresh).some)
  def refresh(refresh: RefreshPolicy): IndexDefinition = copy(refresh = refresh.some)
  def timestamp(timestamp: String): IndexDefinition = copy(timestamp = timestamp.some)
  def routing(routing: String): IndexDefinition = copy(routing = routing.some)

  def version(version: Long): IndexDefinition = copy(version = version.some)
  def versionType(versionType: VersionType): IndexDefinition = copy(versionType = versionType.some)

  def timeout(timeout: String): IndexDefinition = copy(timeout = timeout.some)

  // if set to true then trying to update a document will fail
  def createOnly(createOnly: Boolean): IndexDefinition = copy(createOnly = createOnly.some)

  def fields(_fields: (String, Any)*): IndexDefinition = fields(_fields.toMap)
  def fields(_fields: Iterable[(String, Any)]): IndexDefinition = fields(_fields.toMap)
  def fields(fields: Map[String, Any]): IndexDefinition = copy(fields = FieldsMapper.mapFields(fields))
  def fieldValues(fields: FieldValue*): IndexDefinition = copy(fields = fields)
}

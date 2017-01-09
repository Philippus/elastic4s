package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.bulk.BulkCompatibleDefinition
import com.sksamuel.elastic4s.mappings.FieldValue
import com.sksamuel.elastic4s.{FieldsMapper, Indexable}
import com.sksamuel.exts.OptionImplicits._

object OpType {
  val Index = 0
  val Create = 1
}

object RefreshPolicy {
  val None = "NONE"
  val Wait = "WAIT_UNTIL"
  val Immediate = "IMMEDIATE"
}

case class IndexDefinition(index: String,
                           `type`: String,
                           id: Option[Any] = None,
                           opType: Option[Int] = None,
                           refresh: Option[String] = None,
                           parent: Option[String] = None,
                           pipeline: Option[String] = None,
                           routing: Option[String] = None,
                           timestamp: Option[String] = None,
                           version: Option[Long] = None,
                           fields: Seq[FieldValue] = Nil,
                           source: Option[String] = None) extends BulkCompatibleDefinition {
  require(index != null, "index must not be null or empty")
  require(`type` != null, "type must not be null or empty")

  def doc(json: String): IndexDefinition = source(json)
  def doc[T: Indexable](t: T): IndexDefinition = source(t)

  def source(json: String): IndexDefinition = copy(source = json.some)
  def source[T](t: T)(implicit indexable: Indexable[T]): IndexDefinition = copy(source = indexable.json(t).some)

  def id(id: Any): IndexDefinition = withId(id)
  def withId(id: Any): IndexDefinition = copy(id = id.some)

  def opType(opType: Int): IndexDefinition = copy(opType = opType.some)
  def pipeline(pipeline: String): IndexDefinition = copy(pipeline = pipeline.some)
  def parent(parent: String): IndexDefinition = copy(parent = parent.some)
  def refresh(refresh: String): IndexDefinition = copy(refresh = refresh.some)
  def timestamp(timestamp: String): IndexDefinition = copy(timestamp = timestamp.some)
  def routing(routing: String): IndexDefinition = copy(routing = routing.some)
  def version(version: Long): IndexDefinition = copy(version = version.some)

  // if set to true then trying to update a document will fail
  def createOnly(createOnly: Boolean): IndexDefinition = if (createOnly) opType(OpType.Create) else opType(OpType.Index)

  def fields(_fields: (String, Any)*): IndexDefinition = fields(_fields.toMap)
  def fields(_fields: Iterable[(String, Any)]): IndexDefinition = fields(_fields.toMap)
  def fields(fields: Map[String, Any]): IndexDefinition = copy(fields = FieldsMapper.mapFields(fields))
  def fieldValues(fields: FieldValue*): IndexDefinition = copy(fields = fields)
}

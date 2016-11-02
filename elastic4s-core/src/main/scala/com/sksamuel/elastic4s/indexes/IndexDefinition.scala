package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.mappings.FieldValue
import com.sksamuel.elastic4s.{BulkCompatibleDefinition, DocumentMap, DocumentSource, FieldsMapper, Indexable}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexRequest.OpType
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import org.elasticsearch.index.VersionType

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

class IndexDefinition(index: String, `type`: String) extends BulkCompatibleDefinition {
  require(index != null, "index must not be null or empty")
  require(`type` != null, "type must not be null or empty")

  private val _request = new IndexRequest(index, `type`)
  private val _fields = mutable.Buffer[FieldValue]()
  private var _source: Option[DocumentSource] = None
  private var _json: Option[String] = None
  private var _map: Option[DocumentMap] = None

  def build = _source match {
    case Some(src) => _request.source(src.json)
    case None =>
      _json match {
        case Some(json) => _request.source(json)
        case None =>
          _map match {
            case Some(map) => _request.source(map.map.asJava)
            case None => _request.source(_fieldsAsXContent)
          }
      }
  }

  def _fieldsAsXContent: XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject()
    _fields.foreach(_.output(source))
    source.endObject()
  }

  def source(json: String): this.type = {
    this._json = Option(json)
    this
  }

  def source[T](t: T)(implicit indexable: Indexable[T]): this.type = {
    this._json = Option(indexable.json(t))
    this
  }

  def id(id: Any): IndexDefinition = {
    _request.id(id.toString)
    this
  }

  def withId(id: Any): IndexDefinition = {
    _request.id(id.toString)
    this
  }

  def opType(opType: IndexRequest.OpType): IndexDefinition = {
    _request.opType(opType)
    this
  }

  def parent(parent: String): IndexDefinition = {
    _request.parent(parent)
    this
  }

  def refresh(refresh: RefreshPolicy): this.type = {
    _request.setRefreshPolicy(refresh)
    this
  }

  def routing(routing: String): IndexDefinition = {
    _request.routing(routing)
    this
  }

  def timestamp(timestamp: String): IndexDefinition = {
    _request.timestamp(timestamp)
    this
  }

  def ttl(ttl: Long): IndexDefinition = {
    _request.ttl(ttl)
    this
  }

  def ttl(duration: FiniteDuration): this.type = {
    _request.ttl(duration.toMillis)
    this
  }

  def update(update: Boolean): IndexDefinition = if (update) opType(OpType.CREATE) else opType(OpType.INDEX)

  def version(version: Long): IndexDefinition = {
    _request.version(version)
    this
  }

  def versionType(versionType: VersionType): IndexDefinition = {
    _request.versionType(versionType)
    this
  }

  def fields(fields: Map[String, Any]): IndexDefinition = {
    _fields ++= FieldsMapper.mapFields(fields)
    this
  }

  def fields(_fields: (String, Any)*): IndexDefinition = fields(_fields.toMap)
  def fields(_fields: Iterable[(String, Any)]): IndexDefinition = fields(_fields.toMap)

  def fieldValues(fields: FieldValue*): IndexDefinition = {
    _fields ++= fields
    this
  }
}

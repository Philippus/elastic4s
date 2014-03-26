package com.sksamuel.elastic4s

import org.elasticsearch.index.VersionType
import org.elasticsearch.action.index.IndexRequest.OpType
import org.elasticsearch.common.xcontent.{ XContentFactory, XContentBuilder }
import org.elasticsearch.action.index.{ IndexAction, IndexRequest }
import scala.collection.JavaConverters._
import com.sksamuel.elastic4s.source.{ DocumentMap, DocumentSource, Source }
import scala.collection.mutable

/** @author Stephen Samuel */
trait IndexDsl {

  def insert: IndexExpectsInto = index
  def index: IndexExpectsInto = new IndexExpectsInto

  class IndexExpectsInto {
    def into(index: String): IndexDefinition = into(index.split("/").head, index.split("/").last)
    def into(index: String, `type`: String): IndexDefinition = new IndexDefinition(index, `type`)
    def into(kv: (String, String)): IndexDefinition = into(kv._1, kv._2)
  }

  class IndexDefinition(index: String, `type`: String)
      extends RequestDefinition(IndexAction.INSTANCE) with BulkCompatibleDefinition {

    private val _request = new IndexRequest(index, `type`)
    private val _fields = mutable.Buffer[FieldValue]()
    private var _source: Option[DocumentSource] = None
    private var _map: Option[DocumentMap] = None

    def build = _source match {
      case Some(src) => _request.source(src.json)
      case None => _map match {
        case Some(map) => _request.source(map.map.asJava)
        case None => _request.source(_fieldsAsXContent)
      }
    }

    def _fieldsAsXContent: XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject()
      _fields.foreach(_.output(source))
      source.endObject()
    }

    def id(id: Any): IndexDefinition = {
      _request.id(id.toString)
      this
    }
    def routing(routing: String): IndexDefinition = {
      _request.routing(routing)
      this
    }

    def parent(parent: String): IndexDefinition = {
      _request.parent(parent)
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

    def update(update: Boolean): IndexDefinition = opType(OpType.CREATE)
    def opType(opType: IndexRequest.OpType): IndexDefinition = {
      _request.opType(opType)
      this
    }

    def version(version: Int): IndexDefinition = {
      _request.version(version)
      this
    }

    def versionType(versionType: VersionType): IndexDefinition = {
      _request.versionType(versionType)
      this
    }

    def fields(fields: Map[String, Any]): IndexDefinition = {
      def mapFields(fields: Map[String, Any]): Seq[FieldValue] = {
        fields map {
          case (name: String, nest: Map[_, _]) =>
            val nestedFields = mapFields(nest.asInstanceOf[Map[String, Any]])
            NestedFieldValue(Some(name), nestedFields)

          case (name: String, nest: Array[Map[_, _]]) =>
            val nested = nest.map(n => new NestedFieldValue(None, mapFields(n.asInstanceOf[Map[String, Any]])))
            ArrayFieldValue(name, nested)

          case (name: String, arr: Array[Any]) =>
            val values = arr.map(new SimpleFieldValue(None, _))
            ArrayFieldValue(name, values)

          case (name: String, s: Seq[_]) =>
            s.headOption match {
              case Some(m: Map[_, _]) =>
                val nested = s.map(n => new NestedFieldValue(None, mapFields(n.asInstanceOf[Map[String, Any]])))
                ArrayFieldValue(name, nested)

              case Some(a: Any) =>
                val values = s.map(new SimpleFieldValue(None, _))
                ArrayFieldValue(name, values)

              case _ =>
                // can't work out or empty - map to empty
                ArrayFieldValue(name, Seq())
            }

          case (name: String, a: Any) =>
            SimpleFieldValue(Some(name), a)

          case (name: String, _) =>
            NullFieldValue(name)
        }
      }.toSeq

      _fields ++= mapFields(fields)

      this
    }

    def fields(_fields: (String, Any)*): IndexDefinition = fields(_fields.toMap)
    def fields(_fields: Iterable[(String, Any)]): IndexDefinition = fields(_fields.toMap)

    def fieldValues(fields: FieldValue*): IndexDefinition = {
      _fields ++= fields
      this
    }

    def doc(source: DocumentSource) = {
      this._source = Option(source)
      this
    }

    def doc(map: DocumentMap) = {
      this._map = Option(map)
      this
    }

    @deprecated("renamed to doc", "1.0")
    def source(source: Source) = doc(source)
  }

  trait FieldValue {
    def output(source: XContentBuilder): Unit
  }

  case class NullFieldValue(name: String) extends FieldValue {
    def output(source: XContentBuilder): Unit = {
      source.nullField(name)
    }
  }

  case class SimpleFieldValue(name: Option[String], value: Any) extends FieldValue {
    def output(source: XContentBuilder): Unit = {
      name match {
        case Some(n) => source.field(n, value)
        case None => source.value(value)
      }
    }
  }

  object SimpleFieldValue {
    def apply(name: String, value: Any): SimpleFieldValue = apply(Some(name), value)
    def apply(value: Any): SimpleFieldValue = apply(None, value)
  }

  case class ArrayFieldValue(name: String, values: Seq[FieldValue]) extends FieldValue {
    def output(source: XContentBuilder): Unit = {
      source.startArray(name)
      values.foreach(_.output(source))
      source.endArray()
    }
  }

  case class NestedFieldValue(name: Option[String], values: Seq[FieldValue]) extends FieldValue {
    def output(source: XContentBuilder): Unit = {
      name match {
        case Some(n) => source.startObject(n)
        case None => source.startObject()
      }

      values.foreach(_.output(source))

      source.endObject()
    }
  }

  object NestedFieldValue {
    def apply(name: String, values: Seq[FieldValue]): NestedFieldValue = apply(Some(name), values)
    def apply(values: Seq[FieldValue]): NestedFieldValue = apply(None, values)
  }
}

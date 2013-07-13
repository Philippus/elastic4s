package com.sksamuel.elastic4s

import org.elasticsearch.index.VersionType
import org.elasticsearch.action.index.IndexRequest.OpType
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}
import org.elasticsearch.action.index.IndexRequest
import scala.collection.mutable.ListBuffer
import com.sksamuel.elastic4s.source.Source

/** @author Stephen Samuel */
trait IndexDsl {

  def insert: IndexExpectsInto = index
  def index: IndexExpectsInto = new IndexExpectsInto

  class IndexExpectsInto {
    def into(index: String): IndexDefinition = into(index.split("/").head, index.split("/").last)
    def into(index: String, `type`: String): IndexDefinition = new IndexDefinition(index, `type`)
    def into(kv: (String, String)): IndexDefinition = into(kv._1, kv._2)
  }

  class IndexDefinition(index: String, `type`: String) extends BulkCompatibleRequest {

    private val _request = new IndexRequest(index, `type`)
    private val _fields = new ListBuffer[(String, Any)]
    private var _source: Option[Source] = None
    def build = _source match {
      case None => _request.source(_fieldsAsXContent)
      case Some(src) => _request.source(src.json)
    }

    def _fieldsAsXContent: XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject()
      for ( tuple <- _fields ) {
        source.field(tuple._1, tuple._2)
      }
      source.endObject()
    }

    def id(id: Any) = {
      _request.id(id.toString)
      this
    }
    def routing(routing: String) = {
      _request.routing(routing)
      this
    }

    def parent(parent: String) = {
      _request.parent(parent)
      this
    }

    def timestamp(timestamp: String) = {
      _request.timestamp(timestamp)
      this
    }

    def ttl(ttl: Long) = {
      _request.ttl(ttl)
      this
    }

    def update(update: Boolean) = opType(OpType.CREATE)
    def opType(opType: IndexRequest.OpType) = {
      _request.opType(opType)
      this
    }

    def version(version: Int) = {
      _request.version(version)
      this
    }

    def versionType(versionType: VersionType) = {
      _request.versionType(versionType)
      this
    }

    def fields(map: Map[String, Any]): IndexDefinition = fields(map.toList)
    def fields(_fields: (String, Any)*): IndexDefinition = fields(_fields.toIterable)
    def fields(iterable: Iterable[(String, Any)]): IndexDefinition = {
      this._fields ++= iterable
      this
    }

    def source(source: Source) = {
      this._source = Option(source)
      this
    }
  }
}

package com.sksamuel.elastic4s

import org.elasticsearch.index.VersionType
import org.elasticsearch.action.index.IndexRequest.OpType
import scala.util.DynamicVariable
import scala.collection.mutable.ListBuffer
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}

/** @author Stephen Samuel */
case class IndexedField(name: String, value: Any)
case class IndexReq(index: String,
                    `type`: String,
                    id: Option[String],
                    parent: Option[String] = None,
                    refresh: Boolean = false, // careful
                    routing: Option[String] = None,
                    version: Long = 0,
                    timestamp: Option[String] = None,
                    ttl: Long = 0,
                    versionType: VersionType = VersionType.INTERNAL,
                    opType: OpType = OpType.INDEX,
                    fields: Seq[IndexedField] = Nil) {

    def _source: XContentBuilder = {
        val source = XContentFactory.jsonBuilder().startObject()
        for ( field <- fields ) {
            source.field(field.name, field.value.toString)
        }
        source.endObject()
    }
}

case class IndexResp(ok: Boolean, index: String, `type`: String, id: String, version: Long)

class IndexBuilder2 {
    def into(index: String) = this
    def id(id: Any) = this
    def mapping(`type`: String) = this
    def routing(routing: String) = this
    def parent(parent: String) = this
    def timestamp(timestamp: String) = this
    def version(version: Int) = this
    def opType(opType: OpType) = this
    def ttl(ttl: Long) = this
    def fields(block: => Unit) = this
    def update(update: Boolean) = opType(OpType.CREATE)
    def noupdate = this
}

trait IndexDsl {

    private val indexBuilderContext = new DynamicVariable[Option[IndexBuilder]](None)

    def insert = new IndexBuilder2
    def insert(block: => Unit) = new IndexBuilder2

    def index = new IndexBuilder2
    def index(block: => Unit) = new IndexBuilder2

    def index(idx: String, `type`: String)(block: => Unit): IndexReq = index(idx, `type`, null)(block)
    def index(idx: String, `type`: String, id: String)(block: => Unit): IndexReq = {
        val builder = new IndexBuilder(idx, `type`, Option(id))
        indexBuilderContext.withValue(Some(builder)) {
            block
        }
        builder.build
    }

    def routing(routing: String) {
        indexBuilderContext.value foreach (_._routing = Option(routing))
    }

    def parent(parent: String) {
        indexBuilderContext.value foreach (_._parent = Option(parent))
    }

    def timestamp(timestamp: String) {
        indexBuilderContext.value foreach (_._timestamp = Option(timestamp))
    }

    def version(version: Int) {
        indexBuilderContext.value foreach (_._version = version)
    }

    def opType(opType: OpType) {
        require(opType != null)
        indexBuilderContext.value foreach (_._opType = opType)
    }

    def ttl(ttl: Long) {
        indexBuilderContext.value foreach (_._ttl = ttl)
    }

    def field(name: String, value: Any) {
        indexBuilderContext.value foreach (_._fields.append(IndexedField(name, value)))
    }

    def field(keyValue: (String, Any)) {
        indexBuilderContext.value foreach (_._fields.append(IndexedField(keyValue._1, keyValue._2)))
    }

    def fields(fields: (String, Any)*) {
        fields.foreach(field(_))
    }

    def fields(map: Map[String, Any]) {
        map.foreach(field(_))
    }

    implicit def string2builder(name: String) = new InfixFieldBuilder(name)
    class InfixFieldBuilder(name: String) {
        def ->(value: Any) {
            field(name, value)
        }
    }
}

class IndexBuilder(index: String, `type`: String, id: Option[String]) {

    var _routing: Option[String] = None
    var _parent: Option[String] = None
    var _version = 0
    var _timestamp: Option[String] = None
    var _ttl: Long = 0
    var _versionType: VersionType = VersionType.INTERNAL
    var _opType: OpType = OpType.INDEX
    val _fields = new ListBuffer[IndexedField]()

    def build: IndexReq = {
        IndexReq(index,
            `type`,
            id,
            _parent,
            false,
            _routing,
            _version,
            _timestamp,
            _ttl,
            _versionType,
            _opType,
            _fields)
    }
}
package com.sksamuel.elastic4s

import org.elasticsearch.index.VersionType
import org.elasticsearch.action.index.IndexRequest.OpType
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}
import org.elasticsearch.action.index.{IndexRequest, IndexRequestBuilder}
import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
object IndexDsl {

    def insert: IndexExpectsInto = new IndexExpectsInto
    def index: IndexExpectsInto = new IndexExpectsInto

    class IndexExpectsInto {
        def into(index: String): IndexBuilder = into(index.split("/").toSeq)
        private def into(seq: Seq[String]): IndexBuilder = into(seq(0), seq(1))
        def into(index: String, `type`: String): IndexBuilder = new IndexBuilder(index, `type`)
        def into(kv: (String, String)): IndexBuilder = into(kv._1, kv._2)
    }

    class IndexBuilder(index: String, `type`: String) {

        val builder = new IndexRequestBuilder(null).setIndex(index).setType(`type`)
        val _fields = new ListBuffer[(String, Any)]

        def id(id: Any) = {
            builder.setId(id.toString)
            this
        }
        def routing(routing: String) = {
            builder.setRouting(routing)
            this
        }

        def parent(parent: String) = {
            builder.setParent(parent)
            this
        }

        def timestamp(timestamp: String) = {
            builder.setTimestamp(timestamp)
            this
        }

        def ttl(ttl: Long) = {
            builder.setTTL(ttl)
            this
        }

        def update(update: Boolean) = opType(OpType.CREATE)
        def opType(opType: IndexRequest.OpType) = {
            builder.setOpType(opType)
            this
        }

        def version(version: Int) = {
            builder.setVersion(version)
            this
        }

        def versionType(versionType: VersionType) = {
            builder.setVersionType(versionType)
            this
        }

        def fields(fields: (String, Any)*) = {
            this._fields ++= fields
            this
        }

        def fields(map: Map[String, Any]) = {
            _fields ++= map.toList
            this
        }

        def _source: XContentBuilder = {
            val source = XContentFactory.jsonBuilder().startObject()
            for ( tuple <- _fields ) {
                source.field(tuple._1, tuple._2)
            }
            source.endObject()
        }
    }

    implicit def string2index(indx: String) = new IndexExpectsType(indx)
    class IndexExpectsType(index: String) {
        def /(`type`: String) = (index, `type`)
    }
}

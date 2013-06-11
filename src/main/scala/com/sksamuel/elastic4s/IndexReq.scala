package com.sksamuel.elastic4s

import org.elasticsearch.index.VersionType
import org.elasticsearch.action.index.IndexRequest.OpType

/** @author Stephen Samuel */
case class IndexReq(`type`: String,
                    id: String,
                    parent: Option[String] = None,
                    refresh: Boolean = false, // careful
                    routing: Option[String] = None,
                    version: Long = 0,
                    timestamp: Option[String] = None,
                    ttl: Long = 0,
                    versionType: VersionType = VersionType.INTERNAL,
                    opType: OpType = OpType.INDEX,
                    fields: Seq[IndexedField] = Nil) {

    def field(name: String, value: String): IndexReq = copy(fields = fields :+ IndexedField(name, value))
    def parent(parent: String): IndexReq = copy(parent = Option(parent))
    def refresh(ref: Boolean): IndexReq = copy(refresh = ref)
    def routing(routing: String): IndexReq = copy(routing = Option(routing))
    def version(v: Int): IndexReq = copy(version = v)
    def timestamp(timestamp: String): IndexReq = copy(timestamp = Option(timestamp))
    def ttl(t: Long): IndexReq = copy(ttl = t)
    def versionType(versionType: VersionType): IndexReq = copy(versionType = versionType)
    def opType(op: OpType): IndexReq = copy(opType = op)
}

object IndexReq {
    def apply(`type`: String, id: String) = new IndexReq(`type`, id)
}

case class IndexedField(name: String, value: String)
case class IndexRes(ok: Boolean, index: String, `type`: String, id: String, version: Long)
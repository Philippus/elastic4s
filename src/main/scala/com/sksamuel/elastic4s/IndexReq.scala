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
                    opType: OpType = OpType.INDEX) {
    def parent(parent: String) = copy(parent = Option(parent))
    def refresh(refresh: Boolean) = copy(refresh = refresh)
    def routing(routing: String) = copy(routing = Option(routing))
    def version(version: Int) = copy(version = version)
    def timestamp(timestamp: String) = copy(timestamp = Option(timestamp))
    def timestamp(ttl: Long) = copy(ttl = ttl)
    def versionType(versionType: VersionType) = copy(versionType = versionType)
    def opType(opType: OpType) = copy(opType = opType)
}

object IndexReq {
    def apply(`type`: String, id: String) = new IndexReq(`type`, id)
}

case class IndexRes(ok: Boolean, index: String, `type`: String, id: String, version: Long)
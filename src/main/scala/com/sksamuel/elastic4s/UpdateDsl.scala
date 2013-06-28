package com.sksamuel.elastic4s

import org.elasticsearch.action.update.UpdateRequestBuilder
import org.elasticsearch.action.support.replication.ReplicationType
import org.elasticsearch.action.WriteConsistencyLevel
import org.elasticsearch.common.xcontent.XContentFactory
import scala.concurrent.duration._

/** @author Stephen Samuel */
trait UpdateDsl {

    def update = new UpdateExpectsId
    class UpdateExpectsId {
        def id(id: Any) = new UpdateExpectsIndex(id.toString)
    }
    class UpdateExpectsIndex(id: String) {
        def in(index: String) = new UpdateDefinition(index, id)
    }

    class UpdateDefinition(index: String, id: String) extends BulkCompatibleRequest {
        val _builder = new UpdateRequestBuilder(null).setIndex(index.split("/").head).setType(index.split("/").last).setId(id)
        def build = _builder.request

        def script(script: String): UpdateDefinition = {
            _builder.setScript(script)
            this
        }
        def routing(routing: String): UpdateDefinition = {
            _builder.setRouting(routing)
            this
        }
        def params(map: Map[String, AnyRef]): UpdateDefinition = {
            map.foreach(arg => _builder.addScriptParam(arg._1, arg._2))
            this
        }
        def parent(parent: String): UpdateDefinition = {
            _builder.setParent(parent)
            this
        }
        def refresh(refresh: Boolean): UpdateDefinition = {
            _builder.setRefresh(refresh)
            this
        }
        def replicationType(repType: ReplicationType): UpdateDefinition = {
            _builder.setReplicationType(repType)
            this
        }
        def consistencyLevel(consistencyLevel: WriteConsistencyLevel): UpdateDefinition = {
            _builder.setConsistencyLevel(consistencyLevel)
            this
        }
        def percolate(percolate: String): UpdateDefinition = {
            _builder.setPercolate(percolate)
            this
        }
        def retryOnConflict(retryOnConflict: Int): UpdateDefinition = {
            _builder.setRetryOnConflict(retryOnConflict)
            this
        }
        def lang(scriptLang: String): UpdateDefinition = {
            _builder.setScriptLang(scriptLang)
            this
        }
        def upsert(fields: (String, Any)*): UpdateDefinition = {
            val source = XContentFactory.jsonBuilder().startObject()
            for ( field <- fields ) {
                source.field(field._1, field._2)
            }
            source.endObject()
            _builder.setUpsertRequest(source)
            _builder.setDocAsUpsert(true)
            this
        }

        def upsert(map: Map[String, Any]): UpdateDefinition = upsert(map.toSeq: _*)
    }
}

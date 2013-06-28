package com.sksamuel.elastic4s

import org.elasticsearch.action.update.UpdateRequestBuilder
import org.elasticsearch.action.support.replication.ReplicationType
import org.elasticsearch.action.WriteConsistencyLevel

/** @author Stephen Samuel */
class UpdateDsl {

    def update = new UpdateExpectsId
    class UpdateExpectsId {
        def id(id: Any) {}
    }

    class UpdateDefinition {
        val _builder = new UpdateRequestBuilder(null)
        def script(script: String) = {
            _builder.setScript(script)
            this
        }
        def routing(routing: String) = {
            _builder.setRouting(routing)
            this
        }
        def params(map: Map[String, AnyRef]) = {
            map.foreach(arg => _builder.addScriptParam(arg._1, arg._2))
            this
        }
        def parent(parent: String) = {
            _builder.setParent(parent)
            this
        }
        def refresh(refresh: Boolean) = {
            _builder.setRefresh(refresh)
            this
        }
        def replicationType(repType: ReplicationType) = {
            _builder.setReplicationType(repType)
            this
        }
        def consistencyLevel(consistencyLevel: WriteConsistencyLevel) = {
            _builder.setConsistencyLevel(consistencyLevel)
            this
        }
        def percolate(percolate: String) = {
            _builder.setPercolate(percolate)
            this
        }
    }
}

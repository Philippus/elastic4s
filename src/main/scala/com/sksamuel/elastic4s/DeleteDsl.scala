package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests

/** @author Stephen Samuel */
object DeleteDsl extends QueryDsl {

    def delete = new DeleteExpectsIdOrFrom
    class DeleteExpectsIdOrFrom {
        def from(indexes: String*) = new DeleteByQueryDefinition(indexes: _*)
        def id(id: Any) = new DeleteByIdExpectsFrom(id.toString)
    }

    class DeleteWithIndexExpectsIdOrQuery(from: String) {
    }

    class DeleteByIdExpectsFrom(id: String) {
        def from(from: String) = new DeleteByIdDefinition(id, from)
    }

    class DeleteByIdDefinition(id: String, from: String) {
        val builder = from.split("/").toList match {
            case i :: Nil => Requests.deleteRequest(i).id(id)
            case i :: t :: Nil => Requests.deleteRequest(i).`type`(t).id(id)
            case _ => throw new IllegalArgumentException("from must be in the form index or index/type")
        }
    }

    class DeleteByQueryDefinition(indexes: String*) extends BulkCompatibleRequest {
        val builder = Requests.deleteByQueryRequest(indexes: _*)
        def where(query: String) = {
            val queryStringDef = new StringQueryDefinition(query)
            builder.query(queryStringDef.builder)
            this
        }
        def where(query: QueryDefinition) = {
            builder.query(query.builder)
            this
        }
    }
}

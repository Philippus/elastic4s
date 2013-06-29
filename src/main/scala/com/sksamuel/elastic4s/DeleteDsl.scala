package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests

/** @author Stephen Samuel */
trait DeleteDsl extends QueryDsl {

    def delete = new DeleteExpectsIdOrFrom
    class DeleteExpectsIdOrFrom {
        def from(indexes: String*) = new DeleteByQueryDefinition(indexes: _*)
        def id(id: Any) = new DeleteByIdExpectsFrom(id.toString)
    }

    class DeleteByIdExpectsFrom(id: String) {
        def from(tuple: (String, String)) = new DeleteByIdDefinition(tuple._1, tuple._2, id)
        def from(from: String) = from.split("/").toList match {
            case index :: Nil => new DeleteByIdDefinition(index, null, id)
            case index :: t :: Nil => new DeleteByIdDefinition(index, t, id)
            case _ => throw new IllegalArgumentException("from must be in the form index->type or index/type")
        }
    }

    class DeleteByIdDefinition(index: String, `type`: String, id: String) {
        val builder = Requests.deleteRequest(index).`type`(`type`).id(id)
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

package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests

/** @author Stephen Samuel */
object MoreLikeThisDsl {

    def mlt = new MltExpectingId
    def morelike = new MltExpectingId
    class MltExpectingId {
        def id(id: Any) = new MltExpectsIndex(id.toString)
    }

    class MltExpectsIndex(id: String) {
        def in(in: String) = in.split("/").toList match {
            case idx :: Nil => new MoreLikeThisDefinition(idx, null, id)
            case idx :: t :: Nil => new MoreLikeThisDefinition(idx, t, id)
        }
    }

    class MoreLikeThisDefinition(index: String, `type`: String, id: String) {
        val _builder = Requests.moreLikeThisRequest(index).`type`(`type`).id(id)
        def fields(fields: String*) = {
            _builder.fields(fields: _*)
            this
        }
    }
}

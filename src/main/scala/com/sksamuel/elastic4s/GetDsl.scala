package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests

/** @author Stephen Samuel */
object GetDsl {

    def get = new GetExpectsId
    class GetExpectsId {
        def id(id: Any) = new GetWithIdExpectsFrom(id.toString)
    }
    class GetWithIdExpectsFrom(id: String) {
        def from(index: String): GetBuilder = from(index.split("/"))
        private def from(seq: Seq[String]): GetBuilder = from(seq(0), seq(1))
        def from(index: String, `type`: String): GetBuilder = new GetBuilder(index, `type`, id)
        def from(kv: (String, String)): GetBuilder = from(kv._1, kv._2)
    }

    class GetBuilder(index: String, `type`: String, id: String) {

        val _builder = Requests.getRequest(index).`type`(`type`).id(id)
        def build = _builder

        def routing(r: String) = {
            _builder.routing(r)
            this
        }

        def preference(pref: String) = {
            _builder.preference(pref)
            this
        }

        def preference(pref: Preference) = {
            _builder.preference(pref.elastic)
            this
        }
    }
}

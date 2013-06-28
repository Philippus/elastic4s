package com.sksamuel.elastic4s

import org.elasticsearch.client.Requests
import scala.collection.mutable.ListBuffer
import org.elasticsearch.action.percolate.PercolateRequestBuilder
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}

/** @author Stephen Samuel */
object PercolateDsl extends QueryDsl {

    def percolate = new PercolateExpectsIndex
    class PercolateExpectsIndex {
        def in(index: String) = new PercolateDefinition(index)
    }

    class PercolateDefinition(index: String) {

        private val _fields = new ListBuffer[(String, Any)]

        def build = new PercolateRequestBuilder(null).setIndex(index).setType("type1").setSource(_source).request()

        private[elastic4s] def _source: XContentBuilder = {
            val source = XContentFactory.jsonBuilder().startObject()
            for ( tuple <- _fields ) {
                source.field(tuple._1, tuple._2)
            }
            source.endObject()
        }

        def fields(fields: (String, Any)*) = {
            this._fields ++= fields
            this
        }

        def fields(map: Map[String, Any]) = {
            _fields ++= map.toList
            this
        }
    }

    implicit def string2register(name: String) = new RegisterExpectsIndex

    def register = new RegisterExpectsIndex
    class RegisterExpectsIndex {
        def into(index: String) = new RegisterDefinition(index)
    }

    class RegisterDefinition(index: String) {
        var _query: QueryDefinition = _
        def build = {
            val req = Requests.indexRequest("_percolator").`type`(index)
            req.source(_query.builder.buildAsBytes(), false)
            req
        }
        def query(block: => QueryDefinition): RegisterDefinition = {
            _query = block
            this
        }
        def query(string: String) = {
            _query = new StringQueryDefinition(string)
            this
        }
    }
}

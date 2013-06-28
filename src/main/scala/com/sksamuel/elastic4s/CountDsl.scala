package com.sksamuel.elastic4s

import org.elasticsearch.action.count.CountRequestBuilder
import org.elasticsearch.index.query.QueryBuilders

/** @author Stephen Samuel */
trait CountDsl {

    class CountBuilder(indexes: Seq[String]) {

        val _builder = new CountRequestBuilder(null).setIndices(indexes: _*).setQuery(QueryBuilders.matchAllQuery())
        def build = _builder.request()

        def query(string: String): CountBuilder = {
            val q = new StringQueryDefinition(string)
            _builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def query(block: => QueryDefinition): CountBuilder = {
            _builder.setQuery(block.builder)
            this
        }

        def types(iterable: Iterable[String]): CountBuilder = types(iterable.toSeq: _*)
        def types(types: String*): CountBuilder = {
            _builder.setTypes(types: _*)
            this
        }
    }

    def count = new CountExpectsIndex
    class CountExpectsIndex {
        def from(indexes: Iterable[String]): CountBuilder = new CountBuilder(indexes.toSeq)
        def from(indexes: String*): CountBuilder = new CountBuilder(indexes)
    }
}
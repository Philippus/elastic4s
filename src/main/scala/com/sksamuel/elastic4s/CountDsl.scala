package com.sksamuel.elastic4s

import org.elasticsearch.action.count.CountRequestBuilder

/** @author Stephen Samuel */
object CountDsl {

    class CountBuilder(indexes: Seq[String], types: Seq[String]) {

        val _builder = new CountRequestBuilder(null).setIndices(indexes: _*).setTypes(types: _*)
        def build = _builder.request()

        def query(block: => QueryDefinition): CountBuilder = {
            _builder.setQuery(block.builder)
            this
        }
    }

    def count = new CountExpectsIndex
    class CountExpectsIndex {
        def from(indexes: Iterable[String]): CountExpectsType = new CountExpectsType(indexes.toSeq)
        def from(indexes: String*): CountExpectsType = new CountExpectsType(indexes)
    }

    class CountExpectsType(indexes: Seq[String]) {
        def types(types: String*): CountBuilder = new CountBuilder(indexes, types)
    }
}
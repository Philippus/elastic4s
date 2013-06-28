package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryRequestBuilder

/** @author Stephen Samuel */
trait ValidateDsl extends QueryDsl {

    def validate = new ValidateExpectsIn
    class ValidateExpectsIn {
        def in(value: String): ValidateDefinition = in(value.split("/").toSeq)
        def in(value: Seq[String]): ValidateDefinition = in((value(0), value(1)))
        def in(tuple: (String, String)): ValidateDefinition = new ValidateDefinition(tuple._1, tuple._2)
    }

    class ValidateDefinition(index: String, `type`: String) {
        val _builder = new ValidateQueryRequestBuilder(null).setIndices(index).setTypes(`type`)
        def build = _builder.request

        /**
         * Adds a single string query to this search
         *
         * @param string the query string
         *
         * @return this
         */
        def query(string: String): ValidateDefinition = {
            val q = new StringQueryDefinition(string)
            _builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def query(block: => QueryDefinition): ValidateDefinition = {
            _builder.setQuery(block.builder)
            this
        }
    }
}

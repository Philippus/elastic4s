package com.sksamuel.elastic4s

import org.elasticsearch.index.query.FilterBuilders

/** @author Stephen Samuel */
trait FilterDsl {

    def prefixFilter(field: String, prefix: Any) = new PrefixFilterDefinition(field, prefix)
    def prefixFilter(tuple: (String, Any)) = prefixFilter(tuple._1, tuple._2)

    def termFilter(field: String, prefix: Any): TermFilterDefinition = new TermFilterDefinition(field, prefix)
    def termFilter(tuple: (String, Any)): TermFilterDefinition = termFilter(tuple._1, tuple._2)

    def regexFilter(field: String, prefix: Any): RegexFilterDefinition = new RegexFilterDefinition(field, prefix)
    def regexFilter(tuple: (String, Any)): RegexFilterDefinition = regexFilter(tuple._1, tuple._2)

    def typeFilter(`type`: String): TypeFilterDefinition = new TypeFilterDefinition(`type`)
    def missingFilter(`type`: String): TypeFilterDefinition = new TypeFilterDefinition(`type`)
    def idsFilter(ids: String*): IdFilterDefinition = new IdFilterDefinition(ids: _*)
}

trait FilterDefinition {
    val builder: org.elasticsearch.index.query.FilterBuilder
}

class IdFilterDefinition(ids: String*) extends FilterDefinition {
    val builder = FilterBuilders.idsFilter().addIds(ids: _*)
}

class TypeFilterDefinition(`type`: String) extends FilterDefinition {
    val builder = FilterBuilders.typeFilter(`type`)
}

class MissingFilterDefinition(field: String) extends FilterDefinition {
    val builder = FilterBuilders.missingFilter(field)
}

class PrefixFilterDefinition(field: String, prefix: Any) extends FilterDefinition {
    val builder = FilterBuilders.prefixFilter(field, prefix.toString)
    def cache(cache: Boolean) = {
        builder.cache(cache)
        this
    }
    def cacheKey(cacheKey: String) = {
        builder.cacheKey(cacheKey)
        this
    }
    def name(name: String) = {
        builder.filterName(name)
        this
    }

}

class TermFilterDefinition(field: String, value: Any) extends FilterDefinition {
    val builder = FilterBuilders.termFilter(field, value.toString)
    def cache(cache: Boolean) = {
        builder.cache(cache)
        this
    }
    def cacheKey(cacheKey: String) = {
        builder.cacheKey(cacheKey)
        this
    }
    def name(name: String) = {
        builder.filterName(name)
        this
    }
}

class RegexFilterDefinition(field: String, regex: Any) extends FilterDefinition {
    val builder = FilterBuilders.regexpFilter(field, regex.toString)
    def cache(cache: Boolean) = {
        builder.cache(cache)
        this
    }
    def cacheKey(cacheKey: String) = {
        builder.cacheKey(cacheKey)
        this
    }
    def name(name: String) = {
        builder.filterName(name)
        this
    }
}
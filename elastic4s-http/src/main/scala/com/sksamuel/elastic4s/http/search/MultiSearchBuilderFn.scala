package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.searches.{MultiSearchDefinition, SearchType}

object MultiSearchBuilderFn {
  def apply(request: MultiSearchDefinition): String =
    request.searches
      .flatMap { search =>
        val header = XContentFactory.jsonBuilder()

        header.field("index", search.indexesTypes.indexes.mkString(","))
        search.control.pref.foreach(header.field("preference", _))
        if (search.indexesTypes.types.nonEmpty)
          header.field("type", search.indexesTypes.types.mkString(","))
        search.requestCache.map(_.toString).foreach(header.field("request_cache", _))
        search.searchType
          .filter(_ != SearchType.DEFAULT)
          .map(SearchTypeHttpParameters.convert)
          .foreach(header.field("search_type", _))
        header.endObject()

        val body = SearchBodyBuilderFn(search)

        Seq(header.string(), body.string())

      }
      .mkString("\n") + "\n"
}

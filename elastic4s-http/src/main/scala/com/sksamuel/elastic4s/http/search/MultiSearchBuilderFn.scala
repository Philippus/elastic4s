package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.searches.MultiSearchRequest

object MultiSearchBuilderFn {
  def apply(request: MultiSearchRequest): String =
    request.searches
      .flatMap { search =>
        val header = XContentFactory.jsonBuilder()

        header.field("index", search.indexesTypes.indexes.mkString(","))
        if (search.indexesTypes.types.nonEmpty)
          header.field("type", search.indexesTypes.types.mkString(","))
        search.requestCache.map(_.toString).foreach(header.field("request_cache", _))
        search.searchType.map(_.toString).foreach(header.field("search_type", _))
        header.endObject()

        val body = SearchBodyBuilderFn(search)

        Seq(header.string(), body.string())

      }
      .mkString("\n") + "\n"
}

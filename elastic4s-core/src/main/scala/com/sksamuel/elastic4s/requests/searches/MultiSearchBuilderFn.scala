package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.XContentFactory
import com.sksamuel.elastic4s.requests.common.IndicesOptionsParams

object MultiSearchBuilderFn {

  def apply(request: MultiSearchRequest): String = {

    request.searches.flatMap { search =>
      val header = XContentFactory.jsonBuilder()

      header.field("index", search.indexes.values.mkString(","))
      search.pref.foreach(header.field("preference", _))
      search.requestCache.map(_.toString).foreach(header.field("request_cache", _))
      search.searchType
        .filter(_ != SearchType.DEFAULT)
        .map(SearchTypeHttpParameters.convert)
        .foreach(header.field("search_type", _))

      search.indicesOptions.foreach {
        IndicesOptionsParams(_).map {
          case (n@"ignore_unavailable", v@"true") => header.field(n, v)
          case _ => ()
        }
      }
      header.endObject()

      val body = SearchBodyBuilderFn(search)

      Seq(header.string(), body.string())

    }.mkString("\n") + "\n"
  }
}

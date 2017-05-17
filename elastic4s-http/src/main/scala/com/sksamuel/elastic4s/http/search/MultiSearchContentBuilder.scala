package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.searches.MultiSearchDefinition

object MultiSearchContentBuilder {
  def apply(request: MultiSearchDefinition): String = {
    request.searches.flatMap { search =>

      val header = XContentFactory.jsonBuilder()

      header.field("index", search.indexesTypes.indexes.mkString(","))
      if (search.indexesTypes.types.nonEmpty)
        header.field("type", search.indexesTypes.types.mkString(","))
      search.routing.foreach(header.field("routing", _))
      search.pref.foreach(header.field("preference", _))
      search.searchType.map(_.toString).foreach(header.field("search_type", _))
      header.endObject()

      val body = SearchBodyBuilderFn(search)

      Seq(header.string(), body.string())

    }.mkString("\n") + "\n"
  }
}

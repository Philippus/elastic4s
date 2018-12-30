package com.sksamuel.elastic4s.http.search.template

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.PutSearchTemplateRequest

object PutSearchTemplateBuilderFn {

  def apply(request: PutSearchTemplateRequest): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("template")
    request.body.foreach(builder.rawField("query", _))
    request.query.map(QueryBuilderFn(_)).foreach(builder.rawField("query", _))
    builder.endObject().endObject()
  }
}

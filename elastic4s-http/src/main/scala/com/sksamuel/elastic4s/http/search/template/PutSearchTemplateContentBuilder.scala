package com.sksamuel.elastic4s.http.search.template

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.PutSearchTemplateDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object PutSearchTemplateContentBuilder {

  def apply(request: PutSearchTemplateDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject().startObject("template")
    request.body.map(new BytesArray(_)).foreach(builder.rawField("query", _, XContentType.JSON))
    request.query.map(QueryBuilderFn(_).bytes).foreach(builder.rawField("query", _, XContentType.JSON))
    builder.endObject().endObject()
  }
}

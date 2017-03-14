package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.searches.queries.IdQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

import scala.collection.JavaConverters._

object IdQueryBodyFn {

  def apply(q: IdQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("ids")
    if (q.types.nonEmpty) {
      builder.field("type", q.types.asJava)
    }
    builder.field("values", q.ids.asJava)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}

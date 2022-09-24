package com.sksamuel.elastic4s.handlers.searches.queries.sort

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.sort.FieldSort

object FieldSortBuilderFn {
  def apply(fs: FieldSort): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject(fs.field)

    fs.unmappedType.foreach(builder.field("unmapped_type", _))
    fs.missing.foreach(builder.autofield("missing", _))
    fs.sortMode.map(EnumConversions.sortMode).foreach(builder.field("mode", _))
    builder.field("order", EnumConversions.order(fs.order))

    if (fs.nested.nonEmpty) {
      fs.nested.foreach(n => builder.rawField("nested", NestedSortBuilderFn(n)))
    } else if (fs.nestedPath.nonEmpty || fs.nestedFilter.nonEmpty) {
      builder.startObject("nested")
      fs.nestedPath.foreach(builder.field("path", _))
      fs.nestedFilter.map(f => queries.QueryBuilderFn(f).string).foreach(builder.rawField("filter", _))
      builder.endObject()
    }

    fs.numericType.foreach(builder.field("numeric_type", _))

    builder.endObject().endObject()
  }
}

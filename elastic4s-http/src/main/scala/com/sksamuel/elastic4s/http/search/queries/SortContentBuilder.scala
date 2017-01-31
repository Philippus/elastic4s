package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.sort.{FieldSortDefinition, SortDefinition}
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object SortContentBuilder {
  def apply(sort: SortDefinition): XContentBuilder = sort match {
    case fs: FieldSortDefinition => FieldSortContentBuilder(fs)
  }
}

object FieldSortContentBuilder {
  def apply(fs: FieldSortDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject(fs.field)
    fs.unmappedType.foreach(builder.field("unmapped_type", _))
    fs.missing.foreach(builder.field("missing", _))
    fs.sortMode.map(_.toString).foreach(builder.field("mode", _))
    builder.field("order", fs.order.toString)
    fs.nestedPath.foreach(builder.field("nested_path", _))
    fs.nestedFilter.map(QueryBuilderFn.apply).map(_.string).map(new BytesArray(_)).foreach(builder.rawField("nested_filter", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}

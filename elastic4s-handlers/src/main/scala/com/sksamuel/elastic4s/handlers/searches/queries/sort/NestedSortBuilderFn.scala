package com.sksamuel.elastic4s.handlers.searches.queries.sort

import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.sort.NestedSort

object NestedSortBuilderFn {
  def apply(nested: NestedSort): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    nested.path.foreach(builder.field("path", _))
    nested.filter.foreach(f => builder.rawField("filter", QueryBuilderFn.apply(f)))
    nested.maxChildren.foreach(builder.field("max_children", _))
    nested.nested.foreach(n => builder.rawField("nested", NestedSortBuilderFn(n)))
    builder
  }
}

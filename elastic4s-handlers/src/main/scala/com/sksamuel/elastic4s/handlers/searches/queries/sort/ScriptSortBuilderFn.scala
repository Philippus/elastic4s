package com.sksamuel.elastic4s.handlers.searches.queries.sort

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.sort.ScriptSort

object ScriptSortBuilderFn {

  def apply(scriptSort: ScriptSort): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("_script")

    builder.startObject("script")
    builder.field(scriptSort.script.scriptType.toString.toLowerCase, scriptSort.script.script)
    scriptSort.script.lang.foreach(builder.field("lang", _))
    if (scriptSort.script.params.nonEmpty)
      builder.autofield("params", scriptSort.script.params)
    builder.endObject()

    builder.field("type", scriptSort.scriptSortType.toString.toLowerCase)

    scriptSort.order.map(a => builder.field("order", EnumConversions.order(a)))
    scriptSort.sortMode.map(a => builder.field("mode", EnumConversions.sortMode(a)))
    scriptSort.nested.foreach(n => builder.rawField("nested", NestedSortBuilderFn(n)))

    builder.endObject()
  }
}

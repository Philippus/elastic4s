package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import org.elasticsearch.script.ScriptType

object ScriptBuilderFn {
  def apply(script: ScriptDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject()

    script.lang.foreach(builder.field("lang", _))

    script.scriptType match {
      case ScriptType.FILE => builder.field("file", script.script)
      case ScriptType.INLINE => builder.field("inline", script.script)
    }

    if (script.params.nonEmpty) {
      builder.startObject("params")
      script.params.foreach { case (key, value) =>
        builder.field(key, value)
      }
      builder.endObject()
    }

    if (script.options.nonEmpty) {
      builder.startObject("options")
      script.params.foreach { case (key, value) =>
        builder.field(key, value)
      }
      builder.endObject()
    }
    builder.endObject()
  }
}

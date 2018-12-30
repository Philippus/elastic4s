package com.sksamuel.elastic4s.requests.script

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ScriptBuilderFn {

  def apply(script: Script): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    script.lang.foreach(builder.field("lang", _))

    script.scriptType match {
      case ScriptType.Source => builder.field("source", script.script)
      case ScriptType.Stored => builder.field("id", script.script)
    }

    if (script.params.nonEmpty)
      builder.autofield("params", script.params)

    if (script.options.nonEmpty) {
      builder.startObject("options")
      script.params.foreach {
        case (key, value) =>
          builder.field(key, value.toString)
      }
      builder.endObject()
    }

    builder.endObject()
  }
}

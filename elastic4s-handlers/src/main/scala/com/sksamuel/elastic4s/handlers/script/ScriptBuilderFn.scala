package com.sksamuel.elastic4s.handlers.script

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.script.{Script, ScriptType}

object ScriptBuilderFn {

  def apply(script: Script): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    script.lang.foreach(builder.field("lang", _))

    script.scriptType match {
      case ScriptType.Source => builder.field("source", script.script)
      case ScriptType.Stored => builder.field("id", script.script)
    }

    if (script.params.nonEmpty || script.paramsRaw.nonEmpty) {
      builder.startObject("params")
      script.params.foreach { case (k, v) => builder.autofield(k, v) }
      script.paramsRaw.foreach { case (k, v) => builder.rawField(k, v) }
      builder.endObject()
    }

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

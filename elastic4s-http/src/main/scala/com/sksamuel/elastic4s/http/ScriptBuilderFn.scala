package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.script.{ScriptDefinition, ScriptType}
import scala.collection.JavaConverters._

object ScriptBuilderFn {

  def apply(script: ScriptDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    script.lang.foreach(builder.field("lang", _))

    script.scriptType match {
      case ScriptType.Inline => builder.field("inline", script.script)
    }

    if (script.params.nonEmpty) {
      builder.startObject("params")

      def array(key: String, values: Iterable[_]): Unit = {
        builder.startArray(key)
        values.foreach {
          case (value: String) => builder.value(value)
          case (value: Double) => builder.value(value)
          case (value: Float) => builder.value(value)
          case (value: Int) => builder.value(value)
          case (value: Long) => builder.value(value)
          case (value: Boolean) => builder.value(value)
          case other => builder.value(other.toString)
        }
        builder.endArray()
      }

      script.params.foreach {
        case (key, value: String) => builder.field(key, value)
        case (key, value: Double) => builder.field(key, value)
        case (key, value: Float) => builder.field(key, value)
        case (key, value: Int) => builder.field(key, value)
        case (key, value: Long) => builder.field(key, value)
        case (key, value: Boolean) => builder.field(key, value)
        case (key, values: Seq[_]) => array(key, values)
        case (key, values: Iterator[_]) => array(key, values.toSeq)
        case (key, values: java.util.Collection[_]) => array(key, values.asScala)
        case (key, values: java.util.Iterator[_]) => array(key, values.asScala.toSeq)
        case (key, null) => builder.nullField(key)
        case (key, other) => builder.field(key, other.toString)
      }
      builder.endObject()
    }

    if (script.options.nonEmpty) {
      builder.startObject("options")
      script.params.foreach { case (key, value) =>
        builder.field(key, value.toString)
      }
      builder.endObject()
    }

    builder.endObject()
  }
}

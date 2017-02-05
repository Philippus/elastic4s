package com.sksamuel.elastic4s.http

import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object SourceAsContentBuilder {

  def apply(source: Map[String, Any]): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    def addMap(map: Map[String, Any]): Unit = {
      map.foreach {
        case (key, value: Map[String, Any]) =>
          builder.startObject(key)
          addMap(value)
          builder.endObject()
        case (key, values: Seq[Any]) =>
          builder.startArray(key)
          addSeq(values)
          builder.endArray()
        case (key, value) =>
          builder.field(key, value)
      }
    }

    def addSeq(values: Seq[Any]): Unit = {
      values.foreach {
        case map: Map[String, Any] =>
          builder.startObject()
          addMap(map)
          builder.endObject()
        case seq: Seq[Any] =>
          builder.startArray()
          addSeq(values)
          builder.endArray()
        case other =>
          builder.value(other)
      }
    }

    builder.startObject()
    addMap(source)
    builder.endObject()
    builder
  }
}

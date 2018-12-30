package com.sksamuel.elastic4s

object XContentFieldValueWriter {
  def apply(source: XContentBuilder, value: FieldValue): Unit = value match {
    case NullFieldValue(name) => source.nullField(name)
    case SimpleFieldValue(name, v) =>
      name match {
        case Some(n) => source.autofield(n, v)
        case None    => source.autovalue(v)
      }
    case ArrayFieldValue(name, values) =>
      source.startArray(name)
      values.foreach(apply(source, _))
      source.endArray()
    case NestedFieldValue(name, values) =>
      name match {
        case Some(n) => source.startObject(n)
        case None    => source.startObject()
      }
      values.foreach(apply(source, _))
      source.endObject()
  }
}

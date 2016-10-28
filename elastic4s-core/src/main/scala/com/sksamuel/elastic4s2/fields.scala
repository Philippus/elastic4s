package com.sksamuel.elastic4s2

import org.elasticsearch.common.xcontent.XContentBuilder

trait FieldValue {
  def output(source: XContentBuilder): Unit
}

case class NullFieldValue(name: String) extends FieldValue {
  def output(source: XContentBuilder): Unit = {
    source.nullField(name)
  }
}

case class SimpleFieldValue(name: Option[String], value: Any) extends FieldValue {
  def output(source: XContentBuilder): Unit = {
    name match {
      case Some(n) => source.field(n, value)
      case None => source.value(value)
    }
  }
}

object SimpleFieldValue {
  def apply(name: String, value: Any): SimpleFieldValue = apply(Some(name), value)
  def apply(value: Any): SimpleFieldValue = apply(None, value)
}

case class ArrayFieldValue(name: String, values: Seq[FieldValue]) extends FieldValue {
  def output(source: XContentBuilder): Unit = {
    source.startArray(name)
    values.foreach(_.output(source))
    source.endArray()
  }
}

case class NestedFieldValue(name: Option[String], values: Seq[FieldValue]) extends FieldValue {
  def output(source: XContentBuilder): Unit = {
    name match {
      case Some(n) => source.startObject(n)
      case None => source.startObject()
    }

    values.foreach(_.output(source))

    source.endObject()
  }
}

object NestedFieldValue {
  def apply(name: String, values: Seq[FieldValue]): NestedFieldValue = apply(Some(name), values)
  def apply(values: Seq[FieldValue]): NestedFieldValue = apply(None, values)
}

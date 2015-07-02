package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.XContentBuilder

/** @author liorh  */
object FieldsMapper {
  def mapFields(fields: Map[String, Any]): Seq[FieldValue] = {
    fields map {
      case (name: String, nest: Map[_, _]) =>
        val nestedFields = mapFields(nest.asInstanceOf[Map[String, Any]])
        NestedFieldValue(Some(name), nestedFields)

      case (name: String, nest: Array[Map[_, _]]) =>
        val nested = nest.map(n => new NestedFieldValue(None, mapFields(n.asInstanceOf[Map[String, Any]])))
        ArrayFieldValue(name, nested)

      case (name: String, arr: Array[Any]) =>
        val values = arr.map(new SimpleFieldValue(None, _))
        ArrayFieldValue(name, values)

      case (name: String, a: FieldValue) =>
        NestedFieldValue(name,Seq(a))

      case (name: String, s: Iterable[_]) =>
        s.headOption match {
          case Some(m: Map[_, _]) =>
            val nested = s.map(n => new NestedFieldValue(None, mapFields(n.asInstanceOf[Map[String, Any]])))
            ArrayFieldValue(name, nested.toSeq)

          case Some(a: Any) =>
            val values = s.map(new SimpleFieldValue(None, _))
            ArrayFieldValue(name, values.toSeq)

          case _ =>
            // can't work out or empty - map to empty
            ArrayFieldValue(name, Seq.empty)
        }

      case (name: String, a: Any) =>
        SimpleFieldValue(Some(name), a)

      case (name: String, _) =>
        NullFieldValue(name)
    }
  }.toSeq

}

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

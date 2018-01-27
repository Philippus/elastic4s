package com.sksamuel.elastic4s

trait FieldValue

case class NullFieldValue(name: String)                           extends FieldValue
case class ArrayFieldValue(name: String, values: Seq[FieldValue]) extends FieldValue

case class SimpleFieldValue(name: Option[String], value: Any) extends FieldValue

object SimpleFieldValue {
  def apply(name: String, value: Any): SimpleFieldValue = apply(Some(name), value)
  def apply(value: Any): SimpleFieldValue               = apply(None, value)
}

case class NestedFieldValue(name: Option[String], values: Seq[FieldValue]) extends FieldValue

object NestedFieldValue {
  def apply(name: String, values: Seq[FieldValue]): NestedFieldValue = apply(Some(name), values)
  def apply(values: Seq[FieldValue]): NestedFieldValue               = apply(None, values)
}

trait FieldValueWriter[T <: FieldValue] {
  def write(value: T): Unit
}

package com.sksamuel.elastic4s.json

object XContentFactory {
  def jsonBuilder() = new XContentBuilder
}

class XContentBuilder {
  def string(): String = ???


  def rawValue(value: String) = ???

  def rawField(name: String, builder: XContentBuilder) = ???


  def nullField(name: String) = this
  def field[T](name: String, array: Iterable[T]) = this
  def field(name: String, double: Double) = this
  def field(name: String, str: String) = this
  def field(name: String, boolean: Boolean) = this
  def field(name: String, any: Any) = this

  def value(str: String) = this
  def value(any: Any) = this

  def startArray(name: String) = this
  def startArray() = this
  def endArray() = this

  def startObject() = this
  def startObject(name: String) = this
  def endObject() = this
}

package com.sksamuel.elastic4s.json

object XContentFactory {
  def jsonBuilder() = new XContentBuilder
}

class XContentBuilder {

  def array(field: String, strings: Array[String]) = ???
  def array(field: String, longs: Array[Long]) = ???
  def array(field: String, ints: Array[Int]) = ???
  def array(field: String, floats: Array[Float]) = ???
  def array(field: String, booleans: Array[Boolean]) = ???

  def rawField(name: String, content: String) = ???

  def string(): String = ???
  def bytes: Array[Byte] = ???

  def rawValue(value: String) = ???
  def rawValue(value: XContentBuilder) = ???

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

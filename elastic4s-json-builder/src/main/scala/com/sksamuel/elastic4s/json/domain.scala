package com.sksamuel.elastic4s.json

sealed trait JsonValue

case class ObjectValue(map: scala.collection.mutable.Map[String, JsonValue]) extends JsonValue {

  def putNull(name: String): ObjectValue = {
    map.put(name, NullValue)
    this
  }

  def putArray(name: String): ArrayValue = {
    val array = ArrayValue.empty
    map.put(name, array)
    array
  }

  /**
    * Adds a new object with the given name.
    * Returns the newly created object.
    */
  def putObject(name: String): ObjectValue = {
    val obj = ObjectValue.empty
    map.put(name, obj)
    obj
  }

  /**
    * Adds the given pairing, returning this object.
    */
  def putValue(name: String, value: JsonValue): ObjectValue = {
    map.put(name, value)
    this
  }
}

object ObjectValue {
  def empty: ObjectValue = ObjectValue(scala.collection.mutable.LinkedHashMap.empty[String, JsonValue])
}

case class ArrayValue(elements: scala.collection.mutable.ListBuffer[JsonValue]) extends JsonValue {

  def addNull(): Unit = addValue(NullValue)

  def addValue(element: JsonValue): ArrayValue = copy(elements = elements addOne element)

  def addAll(values: List[JsonValue]): ArrayValue = copy(elements = elements.addAll(values))

  def addArray(): ArrayValue = {
    val array = ArrayValue.empty
    addValue(array)
    array
  }

  def addObject(): ObjectValue = {
    val obj = ObjectValue.empty
    addValue(obj)
    obj
  }
}

object ArrayValue {
  def empty: ArrayValue = ArrayValue(scala.collection.mutable.ListBuffer.empty[JsonValue])
}

case class LongValue(value: Long) extends JsonValue
case class IntValue(value: Int) extends JsonValue
case class DoubleValue(value: Double) extends JsonValue
case class FloatValue(value: Float) extends JsonValue
case class StringValue(value: String) extends JsonValue
case class RawValue(value: String) extends JsonValue
case class BooleanValue(value: Boolean) extends JsonValue
case class BigDecimalValue(value: BigDecimal) extends JsonValue
case class BigIntValue(value: BigInt) extends JsonValue
case object NullValue extends JsonValue

package com.sksamuel.elastic4s.json

import java.util

object XContentFactory {
  def jsonBuilder(): XContentBuilder          = obj()
  def obj()                                   = new XContentBuilder(ObjectValue.empty)
  def array()                                 = new XContentBuilder(ArrayValue.empty)
  def parse(content: String): XContentBuilder = new XContentBuilder(RawValue(content))
}

class XContentBuilder(root: JsonValue) {

  private val stack = new util.ArrayDeque[JsonValue]
  stack.push(root)

  private def current               = stack.peek()
  private def array                 = current.asInstanceOf[ArrayValue]
  private def obj                   = current.asInstanceOf[ObjectValue]
  private def requireArray(): Unit  = require(current.isInstanceOf[ArrayValue])
  private def requireObject(): Unit = require(current.isInstanceOf[ObjectValue])

  def value: JsonValue = root

  // generate a json string from the contents of the builder
  def string: String = JacksonBuilder.writeAsString(root)

  def bytes: Array[Byte] = JacksonBuilder.writeAsString(root).getBytes

  def array(field: String, strings: Array[String]): XContentBuilder = {
    startArray(field)
    strings.map(StringValue.apply).foreach(array.addValue)
    endArray()
    this
  }

  def array(field: String, doubles: Array[Array[Array[Array[Double]]]]): XContentBuilder = {
    startArray(field)
    doubles.foreach { second =>
      val secondArray = array.addArray()
      second.foreach { third =>
        val thirdArray = secondArray.addArray()
        third.foreach { inner =>
          val value = thirdArray.addArray()
          value.addAll(inner.map(DoubleValue.apply).toList)
        }
      }
    }
    endArray()
    this
  }

  def array(field: String, doubles: Array[Array[Array[Double]]]): XContentBuilder = {
    startArray(field)
    doubles.foreach { nested =>
      val outer = array.addArray()
      nested.foreach { inner =>
        val value = outer.addArray()
        value.addAll(inner.map(DoubleValue.apply).toList)
      }
    }
    endArray()
    this
  }

  def array(field: String, doubles: Array[Array[Double]]): XContentBuilder = {
    startArray(field)
    doubles.foreach { nested =>
      val value = array.addArray()
      value.addAll(nested.map(DoubleValue.apply).toList)
    }
    endArray()
    this
  }

  def array(field: String, doubles: Array[Double]): XContentBuilder = {
    startArray(field)
    array.addAll(doubles.map(DoubleValue.apply).toList)
    endArray()
    this
  }

  def array(field: String, strings: List[String]): XContentBuilder = {
    startArray(field)
    array.addAll(strings.map(StringValue.apply))
    endArray()
    this
  }

  def array(field: String, longs: Array[Long]): XContentBuilder = {
    startArray(field)
    array.addAll(longs.map(LongValue.apply).toList)
    endArray()
    this
  }

  def array(field: String, ints: Array[Int]): XContentBuilder = {
    startArray(field)
    array.addAll(ints.map(IntValue.apply).toList)
    endArray()
    this
  }

  def array(field: String, floats: Array[Float]): XContentBuilder = {
    startArray(field)
    array.addAll(floats.map(FloatValue.apply).toList)
    endArray()
    this
  }

  def array(field: String, booleans: Array[Boolean]): XContentBuilder = {
    startArray(field)
    array.addAll(booleans.map(BooleanValue.apply).toList)
    endArray()
    this
  }

  def array(field: String, builder: Array[XContentBuilder]): XContentBuilder = {
    startArray(field)
    builder.foreach { b =>
      val raw = RawValue(b.string)
      array.addValue(raw)
    }
    endArray()
    this
  }

  def rawField(name: String, builder: XContentBuilder): XContentBuilder =
    rawField(name, builder.string)

  def rawField(name: String, content: String): XContentBuilder = {
    obj.putValue(name, RawValue(content))
    this
  }

  def value(value: JsonValue): XContentBuilder = {
    array.addValue(value)
    this
  }

  def rawValue(value: XContentBuilder): this.type = rawValue(value.string)
  def rawValue(value: String): this.type          = {
    array.addValue(RawValue(value))
    this
  }

  def nullField(name: String): XContentBuilder = {
    obj.putNull(name)
    this
  }

  def field(name: String, value: Int): XContentBuilder = {
    obj.putValue(name, IntValue(value))
    this
  }

  def field(name: String, value: Long): XContentBuilder = {
    obj.putValue(name, LongValue(value))
    this
  }

  def field(name: String, value: BigDecimal): XContentBuilder = {
    obj.putValue(name, BigDecimalValue(value.underlying))
    this
  }

  def field(name: String, value: BigInt): XContentBuilder = {
    obj.putValue(name, BigIntValue(value.underlying()))
    this
  }

  def field(name: String, value: Double): XContentBuilder = {
    obj.putValue(name, DoubleValue(value))
    this
  }

  def autovalue(_value: Any): XContentBuilder = {
    import scala.jdk.CollectionConverters._
    requireArray()
    _value match {
      case v: String                       => value(v)
      case v: Double                       => value(v)
      case v: Float                        => value(v)
      case v: Int                          => value(v)
      case v: Long                         => value(v)
      case v: Boolean                      => value(v)
      case v: Short                        => value(v)
      case v: Byte                         => value(v)
      case v: BigDecimal                   => value(v)
      case v: java.math.BigDecimal         => value(v)
      case v: BigInt                       => value(v)
      case v: java.math.BigInteger         => value(v)
      case null                            => array.addNull()
      case values: Seq[_]                  =>
        startArray()
        values.foreach(autovalue)
        endArray()
      case values: Array[_]                => autovalue(values.toSeq)
      case values: Iterator[_]             => autovalue(values.toSeq)
      case values: java.util.Collection[_] => autovalue(values.asScala)
      case values: java.util.Iterator[_]   => autovalue(values.asScala.toSeq)
      case p: Product                      => autovalue(p.productIterator.toList)
      case map: Map[_, _]                  =>
        startObject()
        map.foreach { case (k, v) => autofield(k.toString, v) }
        endObject()
      case map: java.util.Map[_, _]        => autovalue(map.asScala.toMap)
      case other                           => value(other.toString)
    }
    this
  }

  def autoarray(name: String, values: Seq[Any]): XContentBuilder = {
    startArray(name)
    values.foreach(autovalue)
    endArray()
    this
  }

  def autofield(name: String, value: Any): XContentBuilder = {
    import scala.jdk.CollectionConverters._

    value match {
      case v: String                       => field(name, v)
      case v: java.lang.Double             => field(name, v)
      case v: Double                       => field(name, v)
      case v: Float                        => field(name, v)
      case v: Int                          => field(name, v)
      case v: java.lang.Integer            => field(name, v)
      case v: java.lang.Long               => field(name, v)
      case v: Long                         => field(name, v)
      case v: Boolean                      => field(name, v)
      case v: java.lang.Boolean            => field(name, v)
      case v: Short                        => field(name, v)
      case v: Byte                         => field(name, v)
      case v: BigDecimal                   => field(name, v)
      case v: java.math.BigDecimal         => field(name, v)
      case v: BigInt                       => field(name, v)
      case v: java.math.BigInteger         => field(name, v)
      case None                            => obj.putNull(name)
      case values: Array[_]                => autoarray(name, values)
      case values: Seq[_]                  => autoarray(name, values)
      case values: Iterator[_]             => autoarray(name, values.toSeq)
      case values: java.util.Collection[_] => autoarray(name, values.asScala.toSeq)
      case values: java.util.Iterator[_]   => autoarray(name, values.asScala.toSeq)
      case values: Product                 => autoarray(name, values.productIterator.toList)
      case map: Map[_, _]                  =>
        startObject(name)
        map.foreach { case (k, v) => autofield(k.toString, v) }
        endObject()
      case map: java.util.Map[_, _]        => autofield(name, map.asScala.toMap)
      case values: Iterable[_]             => autoarray(name, values.toSeq)
      case null                            => obj.putNull(name)
      case other                           => field(name, other.toString)
    }
    this
  }

  def field(name: String, value: JsonValue): XContentBuilder = {
    // we can only insert fields into objects
    obj.putValue(name, value)
    this
  }

  def field(name: String, value: String): XContentBuilder = {
    // we can only insert fields into objects
    obj.putValue(name, StringValue(value))
    this
  }

  def field(name: String, value: Boolean): XContentBuilder = {
    // we can only insert fields into objects
    obj.putValue(name, BooleanValue(value))
    this
  }

  def value(value: String): XContentBuilder = {
    array.addValue(StringValue(value))
    this
  }

  def value(value: Double): XContentBuilder = {
    array.addValue(DoubleValue(value))
    this
  }

  def value(value: Long): XContentBuilder = {
    array.addValue(LongValue(value))
    this
  }

  def value(value: Float): XContentBuilder = {
    array.addValue(FloatValue(value))
    this
  }

  def value(bd: BigDecimal): XContentBuilder = {
    array.addValue(BigDecimalValue(bd.underlying))
    this
  }

  def value(bi: BigInt): XContentBuilder = {
    array.addValue(BigIntValue(bi.underlying))
    this
  }

  def value(value: Boolean): XContentBuilder = {
    array.addValue(BooleanValue(value))
    this
  }

  def nullValue(): XContentBuilder = {
    array.addNull()
    this
  }

  def startArray(): XContentBuilder = {
    // can only start an anonymous array inside another array
    stack.push(array.addArray())
    this
  }

  def startArray(name: String): XContentBuilder = {
    stack.push(obj.putArray(name))
    this
  }

  def startObject(): XContentBuilder = {
    // can only start an anonymous object if inside an array
    stack.push(array.addObject())
    this
  }

  def startObject(name: String): XContentBuilder = {
    // can only start a named object if inside an array
    stack.push(obj.putObject(name))
    this
  }

  def endArray(): XContentBuilder = {
    requireArray()
    stack.pop()
    this
  }

  def endObject(): XContentBuilder = {
    requireObject()
    stack.pop()
    this
  }
}

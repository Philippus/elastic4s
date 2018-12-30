package com.sksamuel.elastic4s

import java.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.fasterxml.jackson.databind.util.RawValue

object XContentFactory {
  def jsonBuilder(): XContentBuilder          = obj()
  def obj()                                   = new XContentBuilder(JacksonSupport.mapper.createObjectNode)
  def array()                                 = new XContentBuilder(JacksonSupport.mapper.createArrayNode)
  def parse(content: String): XContentBuilder = new XContentBuilder(JacksonSupport.mapper.readTree(content))
}

class XContentBuilder(root: JsonNode) {

  private val stack = new util.ArrayDeque[JsonNode]
  stack.push(root)

  private def current = stack.peek()
  private def array   = current.asInstanceOf[ArrayNode]
  private def obj     = current.asInstanceOf[ObjectNode]

  // generate a json string from the contents of the builder
  def string(): String   = JacksonSupport.mapper.writeValueAsString(root)
  def bytes: Array[Byte] = JacksonSupport.mapper.writeValueAsBytes(root)

  def array(field: String, strings: Array[String]): XContentBuilder = {
    startArray(field)
    strings.foreach(array.add)
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
          inner.foreach(value.add)
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
        inner.foreach(value.add)
      }
    }
    endArray()
    this
  }

  def array(field: String, doubles: Array[Array[Double]]): XContentBuilder = {
    startArray(field)
    doubles.foreach { nested =>
      val value = array.addArray()
      nested.foreach(value.add)
    }
    endArray()
    this
  }

  def array(field: String, doubles: Array[Double]): XContentBuilder = {
    startArray(field)
    doubles.foreach(array.add)
    endArray()
    this
  }

  def array(field: String, longs: Array[Long]): XContentBuilder = {
    startArray(field)
    longs.foreach(array.add)
    endArray()
    this
  }

  def array(field: String, ints: Array[Int]): XContentBuilder = {
    startArray(field)
    ints.foreach(array.add)
    endArray()
    this
  }

  def array(field: String, floats: Array[Float]): XContentBuilder = {
    startArray(field)
    floats.foreach(array.add)
    endArray()
    this
  }

  def array(field: String, booleans: Array[Boolean]): XContentBuilder = {
    startArray(field)
    booleans.foreach(array.add)
    endArray()
    this
  }

  def array(field: String, builder: Array[XContentBuilder]): XContentBuilder = {
    startArray(field)
    builder.foreach { b =>
      val raw = new RawValue(b.string())
      array.addRawValue(raw)
    }
    endArray()
    this
  }

  def rawField(name: String, builder: XContentBuilder): XContentBuilder = rawField(name, builder.string())
  def rawField(name: String, content: String): XContentBuilder = {
    obj.putRawValue(name, new RawValue(content))
    this
  }

  def rawValue(value: XContentBuilder): this.type = rawValue(value.string())
  def rawValue(value: String): this.type = {
    array.addRawValue(new RawValue(value))
    this
  }

  def nullField(name: String): XContentBuilder = {
    obj.putNull(name)
    this
  }

  def field(name: String, int: Int): XContentBuilder = {
    obj.put(name, int)
    this
  }

  def field(name: String, long: Long): XContentBuilder = {
    obj.put(name, long)
    this
  }

  def field(name: String, bd: BigDecimal): XContentBuilder = {
    obj.put(name, bd.underlying)
    this
  }

  def field(name: String, double: Double): XContentBuilder = {
    obj.put(name, double)
    this
  }

  def autovalue(value: Any): XContentBuilder = {
    import scala.collection.JavaConverters._
    requireArray()
    value match {
      case v: String     => array.add(v)
      case v: Double     => array.add(v)
      case v: Float      => array.add(v)
      case v: Int        => array.add(v)
      case v: Long       => array.add(v)
      case v: Boolean    => array.add(v)
      case v: Short      => array.add(v)
      case v: Byte       => array.add(v)
      case v: BigDecimal => array.add(v.bigDecimal)
      case null          => array.addNull()
      case values: Seq[_] =>
        startArray()
        values.foreach(autovalue)
        endArray()
      case values: Array[_]                => autovalue(values.toSeq)
      case values: Iterator[_]             => autovalue(values.toSeq)
      case values: java.util.Collection[_] => autovalue(values.asScala)
      case values: java.util.Iterator[_]   => autovalue(values.asScala.toSeq)
      case map: Map[_, _] =>
        startObject()
        map.foreach { case (k, v) => autofield(k.toString, v) }
        endObject()
      case map: java.util.Map[_, _] => autovalue(map.asScala.toMap)
      case other                    => array.add(other.toString)
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
    import scala.collection.JavaConverters._

    value match {
      case v: String                       => obj.put(name, v)
      case v: java.lang.Double             => obj.put(name, v)
      case v: Double                       => obj.put(name, v)
      case v: Float                        => obj.put(name, v)
      case v: Int                          => obj.put(name, v)
      case v: java.lang.Integer            => obj.put(name, v)
      case v: java.lang.Long               => obj.put(name, v)
      case v: Long                         => obj.put(name, v)
      case v: Boolean                      => obj.put(name, v)
      case v: java.lang.Boolean            => obj.put(name, v)
      case v: Short                        => obj.put(name, v)
      case v: Byte                         => obj.put(name, v)
      case v: BigDecimal                   => obj.put(name, v.bigDecimal)
      case values: Array[_]                => autoarray(name, values)
      case values: Seq[_]                  => autoarray(name, values)
      case values: Iterator[_]             => autoarray(name, values.toSeq)
      case values: java.util.Collection[_] => autoarray(name, values.asScala.toSeq)
      case values: java.util.Iterator[_]   => autoarray(name, values.asScala.toSeq)
      case map: Map[_, _] =>
        startObject(name)
        map.foreach { case (k, v) => autofield(k.toString, v) }
        endObject()
      case map: java.util.Map[_, _] => autofield(name, map.asScala.toMap)
      case null                     => obj.putNull(name)
      case other                    => obj.put(name, other.toString)
    }
    this
  }

  def field(name: String, str: String): XContentBuilder = {
    require(current.isInstanceOf[ObjectNode])
    // we can only insert fields into objects
    current.asInstanceOf[ObjectNode].put(name, str)
    this
  }

  def field(name: String, boolean: Boolean): XContentBuilder = {
    require(current.isInstanceOf[ObjectNode])
    // we can only insert fields into objects
    obj.put(name, boolean)
    this
  }

  def value(str: String): XContentBuilder = {
    array.add(str)
    this
  }

  def value(str: Double): XContentBuilder = {
    array.add(str)
    this
  }

  def value(str: Long): XContentBuilder = {
    array.add(str)
    this
  }

  def value(str: Float): XContentBuilder = {
    array.add(str)
    this
  }

  def value(bd: BigDecimal): XContentBuilder = {
    array.add(bd.underlying)
    this
  }

  def value(str: Boolean): XContentBuilder = {
    array.add(str)
    this
  }

  def nullValue(): XContentBuilder = {
    array.addNull()
    this
  }

  def startArray(): XContentBuilder = {
    // can only start an anoynmous array inside another array
    stack.push(array.addArray())
    this
  }

  def startArray(name: String): XContentBuilder = {
    // can only add a named an anoynmous array inside an object
    stack.push(obj.putArray(name))
    this
  }

  def endArray(): XContentBuilder = {
    require(current.isInstanceOf[ArrayNode])
    stack.pop()
    this
  }

  def startObject(): XContentBuilder = {
    // can only start an object if inside an array
    stack.push(array.addObject())
    this
  }

  def startObject(name: String): XContentBuilder = {
    stack.push(current.asInstanceOf[ObjectNode].putObject(name))
    this
  }

  private def requireArray(): Unit =
    require(current.isInstanceOf[ArrayNode])

  def endObject(): XContentBuilder = {
    require(current.isInstanceOf[ObjectNode])
    stack.pop()
    this
  }
}

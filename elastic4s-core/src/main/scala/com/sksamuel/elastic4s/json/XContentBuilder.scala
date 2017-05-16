package com.sksamuel.elastic4s.json

import java.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.fasterxml.jackson.databind.util.RawValue

object XContentFactory {
  def jsonBuilder(): XContentBuilder = obj()
  def obj() = new XContentBuilder(JacksonSupport.mapper.createObjectNode)
  def array() = new XContentBuilder(JacksonSupport.mapper.createArrayNode)
  def parse(content: String): XContentBuilder = new XContentBuilder(JacksonSupport.mapper.readTree(content))
}

class XContentBuilder(root: JsonNode) {

  private val stack = new util.ArrayDeque[JsonNode]
  stack.push(root)

  private def current = stack.peek()
  private def array = current.asInstanceOf[ArrayNode]
  private def obj = current.asInstanceOf[ObjectNode]

  def array(field: String, strings: Array[String]): XContentBuilder = {
    startArray(field)
    strings.foreach(array.add)
    this
  }

  def array(field: String, doubles: Array[Double]): XContentBuilder = {
    startArray(field)
    doubles.foreach(array.add)
    this
  }

  def array(field: String, longs: Array[Long]): XContentBuilder = {
    startArray(field)
    longs.foreach(array.add)
    this
  }

  def array(field: String, ints: Array[Int]): XContentBuilder = {
    startArray(field)
    ints.foreach(array.add)
    this
  }

  def array(field: String, floats: Array[Float]): XContentBuilder = {
    startArray(field)
    floats.foreach(array.add)
    this
  }

  def array(field: String, booleans: Array[Boolean]): XContentBuilder = {
    startArray(field)
    booleans.foreach(array.add)
    this
  }

  def rawField(name: String, builder: XContentBuilder): XContentBuilder = rawField(name, builder.string)
  def rawField(name: String, content: String): XContentBuilder = {
    obj.putRawValue(name, new RawValue(content))
    this
  }

  def string(): String = JacksonSupport.mapper.writeValueAsString(root)
  def bytes: Array[Byte] = JacksonSupport.mapper.writeValueAsBytes(root)

  def rawValue(value: XContentBuilder): this.type = rawValue(value.string)
  def rawValue(value: String): this.type = {
    array.addRawValue(new RawValue(value))
    this
  }

  def nullField(name: String): XContentBuilder = this

  def field(name: String, any: Any): XContentBuilder = this

  def field(name: String, double: Double): XContentBuilder = {
    require(current.isInstanceOf[ObjectNode])
    // we can only insert fields into objects
    current.asInstanceOf[ObjectNode].put(name, double)
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
    require(current.isInstanceOf[ArrayNode])
    // we can only insert values into lists
    array.add(str)
    this
  }

  def value(any: Any): XContentBuilder = this

  def startArray(name: String): XContentBuilder = {
    stack.push(current.asInstanceOf[ObjectNode].putArray(name))
    this
  }

  def endArray(): XContentBuilder = {
    require(current.isInstanceOf[ArrayNode])
    stack.pop()
    this
  }

  def startObject(name: String): XContentBuilder = {
    stack.push(current.asInstanceOf[ObjectNode].putObject(name))
    this
  }

  def endObject(): XContentBuilder = {
    require(current.isInstanceOf[ObjectNode])
    stack.pop()
    this
  }
}

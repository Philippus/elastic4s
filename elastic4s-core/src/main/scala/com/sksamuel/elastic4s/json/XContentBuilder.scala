package com.sksamuel.elastic4s.json

import java.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}

object XContentFactory {
  def jsonBuilder(): XContentBuilder = obj()
  def obj() = new XContentBuilder(JacksonSupport.mapper.createObjectNode)
  def array() = new XContentBuilder(JacksonSupport.mapper.createArrayNode)
}

class XContentBuilder(root: JsonNode) {

  private val stack = new util.ArrayDeque[JsonNode]
  stack.push(root)

  private def current = stack.peek()

  def array(field: String, strings: Array[String]) = ???
  def array(field: String, longs: Array[Long]) = ???
  def array(field: String, ints: Array[Int]) = ???
  def array(field: String, floats: Array[Float]) = ???
  def array(field: String, booleans: Array[Boolean]) = ???

  def rawField(name: String, content: String) = ???

  def string(): String = JacksonSupport.mapper.writeValueAsString(root)

  def bytes: Array[Byte] = ???

  def rawValue(value: String) = ???
  def rawValue(value: XContentBuilder) = ???

  def rawField(name: String, builder: XContentBuilder) = ???

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
    current.asInstanceOf[ObjectNode].put(name, boolean)
    this
  }

  def value(str: String): XContentBuilder = {
    require(current.isInstanceOf[ArrayNode])
    // we can only insert values into lists
    current.asInstanceOf[ArrayNode].add(str)
    this
  }

  def value(any: Any): XContentBuilder = this

  def startArray(name: String): XContentBuilder = this

  def startArray(): XContentBuilder = ???

  def endArray(): XContentBuilder = ???

  def startObject(): XContentBuilder = ???

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

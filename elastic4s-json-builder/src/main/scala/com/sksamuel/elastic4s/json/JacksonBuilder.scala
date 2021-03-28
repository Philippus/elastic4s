package com.sksamuel.elastic4s.json

import com.fasterxml.jackson.databind.{JsonNode, util}
import com.fasterxml.jackson.databind.node.{BigIntegerNode, BooleanNode, DecimalNode, DoubleNode, FloatNode, IntNode, JsonNodeFactory, LongNode, NullNode, TextNode}
import com.sksamuel.elastic4s.JacksonSupport

trait JsonBuilder {
  def writeAsString(value: JsonValue): String
}

object JacksonBuilder extends JsonBuilder {

  override def writeAsString(value: JsonValue): String = {
    toNode(value).fold(
      {
        JacksonSupport.mapper.writeValueAsString
      },
      {
        JacksonSupport.mapper.writeValueAsString
      }
    )
  }

  def toNode(value: JsonValue): scala.util.Either[JsonNode, util.RawValue] = {
    value match {
      case array: ArrayValue =>
        val node = JacksonSupport.mapper.createArrayNode()
        array.elements.foreach { it =>
          toNode(it).fold(node.add, node.addRawValue)
        }
        Left(node)
      case obj: ObjectValue =>
        val node = JacksonSupport.mapper.createObjectNode()
        obj.map.foreach { case (name, value) =>
          toNode(value).fold(
            { it => node.replace(name, it) },
            { it => node.putRawValue(name, it) }
          )
        }
        Left(node)
      case StringValue(value) => Left(TextNode.valueOf(value))
      case LongValue(value) => Left(LongNode.valueOf(value))
      case IntValue(value) => Left(IntNode.valueOf(value))
      case FloatValue(value) => Left(FloatNode.valueOf(value))
      case DoubleValue(value) => Left(DoubleNode.valueOf(value))
      case BooleanValue(value) => Left(BooleanNode.valueOf(value))
      case BigDecimalValue(value) => Left(JsonNodeFactory.instance.numberNode(value.underlying()))
      case BigIntValue(value) => Left(JsonNodeFactory.instance.numberNode(value.underlying()))
      case RawValue(value) => Right(new com.fasterxml.jackson.databind.util.RawValue(value))
      case NullValue => Left(NullNode.instance)
    }
  }
}

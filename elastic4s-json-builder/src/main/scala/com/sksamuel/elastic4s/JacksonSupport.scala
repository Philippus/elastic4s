package com.sksamuel.elastic4s

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}

object JacksonSupport {

  val mapper: ObjectMapper with ClassTagExtensions = new ObjectMapper with ClassTagExtensions
  mapper.registerModule(DefaultScalaModule)

  mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
  mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
  mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
  mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)

//  class Deserializer(vc: Class[_]) extends StdDeserializer[Total](vc) {
//    override def deserialize(p: JsonParser, ctxt: DeserializationContext): Total = {
//      val node: JsonNode = p.getCodec.readTree(p)
//      if (node.isNumber) Total(node.toString.toLong, "eq")
//      else Total(node.findValue("value").asLong, node.findValue("relation").asText())
//    }
//  }
//
//  val module = new SimpleModule {
//    addDeserializer(classOf[Total], new Total.Deserializer(classOf[Total]))
//  }
//  mapper.registerModule(module)
}

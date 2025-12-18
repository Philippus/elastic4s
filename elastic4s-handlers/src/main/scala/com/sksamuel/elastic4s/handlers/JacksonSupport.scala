package com.sksamuel.elastic4s.handlers

import com.fasterxml.jackson.annotation.JsonInclude
import tools.jackson.core.json.{JsonFactory, JsonReadFeature}
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.{DeserializationFeature, ObjectMapper}
import tools.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}

object JacksonSupport {
  val mapper: ObjectMapper with ClassTagExtensions = {
    val jf = JsonFactory.builder()
      .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
      .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
      .build()
    JsonMapper.builder(jf)
      .addModule(DefaultScalaModule)
      .changeDefaultPropertyInclusion(_.withValueInclusion(JsonInclude.Include.NON_NULL))
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
      .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
      .build() :: ClassTagExtensions
  }
}

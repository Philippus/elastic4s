package com.sksamuel.elastic4s.jackson

import com.sksamuel.elastic4s.Reader
import com.sksamuel.elastic4s.source.Indexable

object ElasticJackson {
  object Implicits {
    implicit object JacksonJsonIndexable extends Indexable[Any] {
      override def json(t: Any): String = JacksonJson.mapper.writeValueAsString(t)
    }
    implicit object JacksonJsonReader extends Reader[Any] {
      override def read[T <: Any : Manifest](json: String): T = JacksonJson.mapper.readValue[T](json)
    }
  }
}

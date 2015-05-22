package com.sksamuel.elastic4s.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.sksamuel.elastic4s.source.DocumentSource

/** @author Stephen Samuel */
@deprecated("Prefer Indexable[T] typeclass", "1.5.12")
class ObjectSource(any: Any) extends DocumentSource {
  def json: String = ObjectSource.mapper.writeValueAsString(any)
}

object ObjectSource {
  val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)
  @deprecated("Prefer Indexable[T] typeclass", "1.5.12")
  def apply(any: Any) = new ObjectSource(any)
}

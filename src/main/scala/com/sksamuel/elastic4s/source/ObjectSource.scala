package com.sksamuel.elastic4s.source

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/** @author Stephen Samuel */
class ObjectSource(any: AnyRef) extends Source {
    def json: String = ObjectSource.mapper.writeValueAsString(any)
}
object ObjectSource {
    val mapper = new ObjectMapper
    mapper.registerModule(DefaultScalaModule)
    def apply(any: AnyRef) = new ObjectSource(any)
}

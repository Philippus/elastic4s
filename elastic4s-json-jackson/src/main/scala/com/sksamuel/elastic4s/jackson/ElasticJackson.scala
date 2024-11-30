package com.sksamuel.elastic4s.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, JavaTypeable}
import com.sksamuel.elastic4s._
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

object ElasticJackson {

  object Implicits {

    protected val logger: Logger = LoggerFactory.getLogger(getClass.getName)

    implicit def JacksonJsonIndexable[T](implicit mapper: ObjectMapper = JacksonSupport.mapper): Indexable[T] =
      (t: T) => mapper.writeValueAsString(t)

    implicit def JacksonJsonParamSerializer[T](implicit
        mapper: ObjectMapper = JacksonSupport.mapper
    ): ParamSerializer[T] =
      (t: T) => mapper.writeValueAsString(t)

    implicit def JacksonJsonHitReader[T](implicit
        mapper: ObjectMapper with ClassTagExtensions = JacksonSupport.mapper,
        javaTypeable: JavaTypeable[T]
    ): HitReader[T] = (hit: Hit) =>
      Try {
        val node = mapper.readTree(mapper.writeValueAsBytes(hit.sourceAsMap)).asInstanceOf[ObjectNode]
        if (!node.has("_id")) node.put("_id", hit.id)
        if (!node.has("_index")) node.put("_index", hit.index)
        //  if (!node.has("_score")) node.put("_score", hit.score)
        if (!node.has("_version")) node.put("_version", hit.version)
        if (!node.has("_seq_no")) node.put("_seq_no", hit.seqNo)
        if (!node.has("_primary_term")) node.put("_primary_term", hit.primaryTerm)
        if (!node.has("_timestamp"))
          hit
            .sourceFieldOpt("_timestamp")
            .collect {
              case f => f.toString
            }
            .foreach(node.put("_timestamp", _))
        mapper.readValue[T](mapper.writeValueAsBytes(node))
      }
  }
}

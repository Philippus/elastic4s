package com.sksamuel.elastic4s.jackson

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.sksamuel.elastic4s._
import com.sksamuel.exts.Logging

import scala.util.control.NonFatal

object ElasticJackson {

  object Implicits extends Logging {

    implicit def JacksonJsonIndexable[T](implicit mapper: ObjectMapper = JacksonSupport.mapper): Indexable[T] = {
      new Indexable[T] {
        override def json(t: T): String = mapper.writeValueAsString(t)
      }
    }

    implicit def JacksonJsonHitReader[T](implicit mapper: ObjectMapper = JacksonSupport.mapper,
                                         manifest: Manifest[T]): HitReader[T] = new HitReader[T] {
      override def read(hit: Hit): Either[Throwable, T] = {
        require(hit.sourceAsString != null)
        try {
          val node = mapper.readTree(hit.sourceAsString).asInstanceOf[ObjectNode]
          if (!node.has("_id")) node.put("_id", hit.id)
          if (!node.has("_type")) node.put("_type", hit.`type`)
          if (!node.has("_index")) node.put("_index", hit.index)
          //  if (!node.has("_score")) node.put("_score", hit.score)
          if (!node.has("_version")) node.put("_version", hit.version)
          if (!node.has("_timestamp")) hit.sourceFieldOpt("_timestamp").collect {
            case f => f.toString
          }.foreach(node.put("_timestamp", _))
          Right(mapper.readValue[T](mapper.writeValueAsBytes(node), manifest.runtimeClass.asInstanceOf[Class[T]]))
        } catch {
          case NonFatal(e) => Left(e)
        }
      }
    }
  }
}


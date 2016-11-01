package com.sksamuel.elastic4s2.jackson

import com.fasterxml.jackson.databind.node.ObjectNode
import com.sksamuel.elastic4s2.search.RichSearchHit
import com.sksamuel.elastic4s2.{Hit, HitAs, HitReader, Indexable}

object ElasticJackson {
  object Implicits {

    import JacksonJson._

    implicit def JacksonJsonIndexable[T]: Indexable[T] = new Indexable[T] {
      override def json(t: T): String = mapper.writeValueAsString(t)
    }

    implicit def JacksonJsonHitReader[T: Manifest]: HitReader[T] = new HitReader[T] {
      override def read(hit: Hit): Either[Exception, T] = {
        try {
          val node = mapper.readTree(hit.sourceAsString).asInstanceOf[ObjectNode]
          if (!node.has("_id")) node.put("_id", hit.id)
          if (!node.has("_type")) node.put("_type", hit.`type`)
          if (!node.has("_index")) node.put("_index", hit.index)
          //  if (!node.has("_score")) node.put("_score", hit.score)
          if (!node.has("_version")) node.put("_version", hit.version)
          if (!node.has("_timestamp")) hit.fieldOpt("_timestamp").collect {
            case f => f.value.toString
          }.foreach(node.put("_timestamp", _))
          Right(mapper.readValue[T](mapper.writeValueAsBytes(node)))
        }
      }
    }

    @deprecated("use Reader which can be used for both get and search APIs", "3.0.0")
    implicit def JacksonJsonHitAs[T: Manifest]: HitAs[T] = new HitAs[T] {
      override def as(hit: RichSearchHit): T = {
        val node = mapper.readTree(hit.sourceAsString).asInstanceOf[ObjectNode]
        if (!node.has("_id")) node.put("_id", hit.id)
        if (!node.has("_type")) node.put("_type", hit.`type`)
        if (!node.has("_index")) node.put("_index", hit.index)
        if (!node.has("_score")) node.put("_score", hit.score)
        if (!node.has("_version")) node.put("_version", hit.version)
        if (!node.has("_timestamp")) hit.fieldOpt("_timestamp").collect {
          case f => f.getValue.toString
        }.foreach(node.put("_timestamp", _))
        mapper.readValue[T](mapper.writeValueAsBytes(node))
      }
    }
  }
}


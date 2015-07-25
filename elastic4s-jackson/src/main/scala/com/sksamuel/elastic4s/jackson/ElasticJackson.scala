package com.sksamuel.elastic4s.jackson

import com.fasterxml.jackson.databind.node.ObjectNode
import com.sksamuel.elastic4s.{RichSearchHit, HitAs, Reader}
import com.sksamuel.elastic4s.source.Indexable

object ElasticJackson {
  object Implicits {

    import JacksonJson._

    implicit def JacksonJsonIndexable[T]: Indexable[T] = new Indexable[T] {
      override def json(t: T): String = mapper.writeValueAsString(t)
    }

    @deprecated("use HitAs, which doesn't have a broken contravariance implementation", "1.6.1")
    implicit object JacksonJsonReader extends Reader[Any] {
      override def read[T <: Any : Manifest](json: String): T = mapper.readValue[T](json)
    }

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


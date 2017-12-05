package com.sksamuel.elastic4s.http

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.json.JacksonSupport

case class ElasticError(`type`: String,
                        reason: String,
                        @JsonProperty("index_uuid") indexUuid: Option[String],
                        index: Option[String],
                        shard: Option[String],
                        @JsonProperty("root_cause") rootCause: Seq[ElasticError])

object ElasticError {

  def fromThrowable(t: Throwable) = ElasticError(t.getClass.getCanonicalName, t.getLocalizedMessage, None, None, None, Nil)

  def parse(r: HttpResponse): ElasticError = {
    r.entity match {
      case Some(entity) =>
        val node = JacksonSupport.mapper.readTree(entity.content)
        if (node.has("error")) {
          val errorNode = node.get("error")
          JacksonSupport.mapper.readValue[ElasticError](JacksonSupport.mapper.writeValueAsBytes(errorNode))
        } else {
          ElasticError(r.statusCode.toString, r.statusCode.toString, None, None, None, Nil)
        }
      case _ =>
        ElasticError(r.statusCode.toString, r.statusCode.toString, None, None, None, Nil)
    }
  }
}

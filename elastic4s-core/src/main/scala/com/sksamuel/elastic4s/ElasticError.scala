package com.sksamuel.elastic4s

import com.fasterxml.jackson.annotation.{JsonAnySetter, JsonProperty}

import scala.collection.mutable

case class ElasticError(`type`: String,
                        reason: String,
                        @JsonProperty("index_uuid") indexUuid: Option[String],
                        index: Option[String],
                        shard: Option[String],
                        @JsonProperty("root_cause") rootCause: Seq[ElasticError],
                        @JsonProperty("caused_by") causedBy: Option[ElasticError.CausedBy])

object ElasticError {

  class CausedBy(val `type`: String,
                 val reason: String,
                 @JsonProperty("script_stack") val scriptStack: Seq[String],
                 @JsonProperty("caused_by") val causedBy: Option[ElasticError.CausedBy]){
    private val _other = mutable.HashMap[String, String]()

    //noinspection ScalaUnusedSymbol
    @JsonAnySetter private def setOther(k: String, v: String): Unit = _other.put(k, v)

    def other(key: String): Option[String] = _other.get(key)

    override def toString: String = s"CausedBy(${`type`},$reason,${_other})"
  }

  def fromThrowable(t: Throwable) =
    ElasticError(t.getClass.getCanonicalName, t.getLocalizedMessage, None, None, None, Nil, None)

  def parse(r: HttpResponse): ElasticError =
    r.entity match {
      case Some(entity) =>
        val node = JacksonSupport.mapper.readTree(entity.content)
        if (node != null && node.has("error")) {
          val errorNode = node.get("error")
          JacksonSupport.mapper.readValue[ElasticError](JacksonSupport.mapper.writeValueAsBytes(errorNode))
        } else
          ElasticError(r.statusCode.toString, r.statusCode.toString, None, None, None, Nil, None)
      case _ =>
        ElasticError(r.statusCode.toString, r.statusCode.toString, None, None, None, Nil, None)
    }
}

package com.sksamuel.elastic4s

import com.fasterxml.jackson.annotation.{JsonAnySetter, JsonProperty}

import scala.collection.mutable

case class ElasticError(`type`: String,
                        reason: String,
                        @JsonProperty("index_uuid") indexUuid: Option[String],
                        index: Option[String],
                        shard: Option[String],
                        @JsonProperty("root_cause") rootCause: Seq[ElasticError],
                        @JsonProperty("caused_by") causedBy: Option[ElasticError.CausedBy],
                        phase: Option[String] = None,
                        grouped: Option[Boolean] = None,
                        @JsonProperty("failed_shards") failedShards: Seq[FailedShard] = Seq()
) {
  def asException: Exception =
    causedBy.fold(new RuntimeException(s"${`type`} $reason", Option(rootCause).flatMap(_.headOption).map(_.asException).orNull))(cause => new RuntimeException(s"${`type`} $reason", new RuntimeException(cause.toString)))
}

case class FailedShard(
  shard: Int,
  index: Option[String],
  node: Option[String],
  reason: Option[ElasticError] // reason is a nested ElasticError here, rather than a string as it is in ElasticError
)

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
}

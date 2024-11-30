package com.sksamuel.elastic4s

import com.fasterxml.jackson.annotation.{JsonAnySetter, JsonProperty}

import scala.collection.mutable

// https://github.com/elastic/elasticsearch-specification/blob/b8b9d95dd6f94dc4e415d37da97095278f9a3a90/specification/_types/Errors.ts#L29
case class ErrorCause(
    `type`: String,
    reason: Option[String],
    @JsonProperty("stack_trace") stackTrace: Option[String],
    @JsonProperty("caused_by") causedBy: Option[ErrorCause],
    @JsonProperty("root_cause") rootCause: Option[Seq[ErrorCause]],
    suppressed: Option[Seq[ErrorCause]]
) {
  private val _other = mutable.HashMap[String, String]()

  // noinspection ScalaUnusedSymbol
  @JsonAnySetter private def setOther(k: String, v: String): Unit = _other.put(k, v)

  def other(key: String): Option[String] = _other.get(key)

  override def toString: String = s"ErrorCause(${`type`},$reason,${_other})"
}

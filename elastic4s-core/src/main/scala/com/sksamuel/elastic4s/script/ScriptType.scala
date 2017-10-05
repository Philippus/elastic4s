package com.sksamuel.elastic4s.script

sealed trait ScriptType
object ScriptType {
  def valueOf(str: String): ScriptType = str.toLowerCase match {
    case "inline" => Inline
    case "source" => Source
    case "stored" => Stored
  }
  @deprecated("use Source", "6.0.0")
  case object Inline extends ScriptType

  case object Source extends ScriptType
  case object Stored extends ScriptType
}

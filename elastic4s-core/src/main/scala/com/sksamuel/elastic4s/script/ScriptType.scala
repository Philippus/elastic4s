package com.sksamuel.elastic4s.script

sealed trait ScriptType
object ScriptType {
  def valueOf(str: String): ScriptType = str.toLowerCase match {
    case "inline" => Inline
    case "stored" => Stored
  }
  case object Inline extends ScriptType
  case object Stored extends ScriptType
}

package com.sksamuel.elastic4s.requests.script

sealed trait ScriptType
object ScriptType {

  def valueOf(str: String): ScriptType = str.toLowerCase match {
    case "source" => Source
    case "stored" => Stored
  }

  case object Source extends ScriptType
  case object Stored extends ScriptType
}

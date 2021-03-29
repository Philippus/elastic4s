package com.sksamuel.elastic4s.requests.searches.sort

sealed trait ScriptSortType

object ScriptSortType {
  def valueOf(str: String): ScriptSortType = str.toLowerCase match {
    case "string" => String
    case "number" => Number
  }
  case object String extends ScriptSortType
  case object Number extends ScriptSortType

  val STRING: ScriptSortType = String
  val NUMBER: ScriptSortType = Number
}

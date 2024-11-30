package com.sksamuel.elastic4s.requests.searches.queries.matches

sealed trait ZeroTermsQuery

object ZeroTermsQuery {
  def valueOf(str: String): ZeroTermsQuery = str.toLowerCase match {
    case "none" => None
    case "all"  => All
  }
  case object All extends ZeroTermsQuery
  case object None extends ZeroTermsQuery

  def ALL: All.type   = All
  def NONE: None.type = None
}

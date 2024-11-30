package com.sksamuel.elastic4s.requests.searches.queries.matches

sealed trait MultiMatchQueryBuilderType

object MultiMatchQueryBuilderType {

  def valueOf(str: String): MultiMatchQueryBuilderType = str.toUpperCase match {
    case "BEST_FIELDS"   => BEST_FIELDS
    case "MOST_FIELDS"   => MOST_FIELDS
    case "CROSS_FIELDS"  => CROSS_FIELDS
    case "PHRASE"        => PHRASE
    case "PHRASE_PREFIX" => PHRASE_PREFIX
    case "BOOL_PREFIX"   => BOOL_PREFIX
  }

  case object BEST_FIELDS   extends MultiMatchQueryBuilderType
  case object MOST_FIELDS   extends MultiMatchQueryBuilderType
  case object CROSS_FIELDS  extends MultiMatchQueryBuilderType
  case object PHRASE        extends MultiMatchQueryBuilderType
  case object PHRASE_PREFIX extends MultiMatchQueryBuilderType
  case object BOOL_PREFIX   extends MultiMatchQueryBuilderType
}

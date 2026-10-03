package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.json.JsonValue

/** A typeclass that is used to build the json bodies for requests.
  *
  * They accept a request instance, such as CountRequest or SearchRequest and return a [[JsonValue]] which models the
  * json to be used.
  */
trait BodyBuilder[R] {
  def toJson(req: R): JsonValue
}

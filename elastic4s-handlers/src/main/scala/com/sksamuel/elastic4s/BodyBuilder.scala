package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.json.{JsonValue, XContentFactory}
import com.sksamuel.elastic4s.requests.count.CountRequest

/** A typeclass that is used to build the json bodies for requests.
  *
  * They accept a request instance, such as CountRequest or SearchRequest and return a [[JsonValue]] which models the
  * json to be used.
  */
trait BodyBuilder[R] {
  def toJson(req: R): JsonValue
}

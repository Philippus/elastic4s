package com.sksamuel.elastic4s

import play.api.libs.json.{Json, Reads, Writes}

import scala.annotation.implicitNotFound
import scala.util.Try

package object playjson {

  @implicitNotFound("No Writes for type ${T} found. Bring an implicit Writes[T] instance in scope")
  implicit def playJsonIndexable[T](implicit w: Writes[T]): Indexable[T] =
    (t: T) => Json.stringify(Json.toJson(t)(w))

  @implicitNotFound("No Reads for type ${T} found. Bring an implicit Reads[T] instance in scope")
  implicit def playJsonHitReader[T](implicit r: Reads[T]): HitReader[T] =
    (hit: Hit) =>
      Try {
        Json.parse(hit.sourceAsString).as[T]
      }

  @implicitNotFound("No Reads for type ${T} found. Bring an implicit Reads[T] instance in scope")
  implicit def playJsonAggReader[T](implicit r: Reads[T]): AggReader[T] =
    (json: String) =>
      Try {
        Json.parse(json).as[T]
      }

  @implicitNotFound("No Writes for type ${T} found. Bring an implicit Writes[T] instance in scope")
  implicit def playJsonParamSerializer[T](implicit w: Writes[T]): ParamSerializer[T] =
    (t: T) => Json.stringify(Json.toJson(t)(w))
}

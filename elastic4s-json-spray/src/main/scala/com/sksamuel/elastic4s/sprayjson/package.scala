package com.sksamuel.elastic4s

import spray.json._

import scala.annotation.implicitNotFound
import scala.util.Try

package object sprayjson {

  @implicitNotFound("No RootJsonWriter for type ${T} found. Bring an implicit RootJsonWriter[T] instance in scope")
  implicit def sprayJsonIndexable[T](implicit w: RootJsonWriter[T]): Indexable[T] =
    (t: T) =>
      w.write(t).compactPrint

  @implicitNotFound("No RootJsonReader for type ${T} found. Bring an implicit RootJsonReader[T] instance in scope")
  implicit def sprayJsonHitReader[T](implicit r: RootJsonReader[T]): HitReader[T] =
    (hit: Hit) =>
      Try {
        r.read(hit.sourceAsString.parseJson)
      }

  @implicitNotFound("No RootJsonReader for type ${T} found. Bring an implicit RootJsonReader[T] instance in scope")
  implicit def sprayJsonAggReader[T](implicit r: RootJsonReader[T]): AggReader[T] =
    (json: String) =>
      Try {
        r.read(json.parseJson)
      }

  @implicitNotFound("No RootJsonWriter for type ${T} found. Bring an implicit RootJsonWriter[T] instance in scope")
  implicit def sprayJsonParamSerializer[T](implicit w: RootJsonWriter[T]): ParamSerializer[T] =
    (t: T) =>
      w.write(t).compactPrint
}

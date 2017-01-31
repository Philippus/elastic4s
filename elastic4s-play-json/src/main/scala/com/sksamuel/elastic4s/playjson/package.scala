package com.sksamuel.elastic4s

import play.api.libs.json.{Json, Reads, Writes}

import scala.annotation.implicitNotFound
import scala.reflect.Manifest
import scala.util.control.NonFatal

package object playjson {

  implicit def format[T](implicit r: Reads[T],
                         w: Writes[T],
                         mf: Manifest[T]): JsonFormat[T] = new JsonFormat[T] {
    override def fromJson(json: String): T = Json.parse(json).as[T]
  }

  @implicitNotFound("No Writes for type ${T} found. Bring an implicit Writes[T] instance in scope")
  implicit def playJsonIndexable[T](implicit w: Writes[T]) = new Indexable[T] {
    override def json(t: T): String = Json.stringify(Json.toJson(t)(w))
  }

  @implicitNotFound("No Reads for type ${T} found. Bring an implicit Reads[T] instance in scope")
  implicit def playJsonHitReader[T](implicit r: Reads[T]) = new HitReader[T] {
    override def read(hit: Hit): Either[Throwable, T] = try {
      Right(Json.parse(hit.sourceAsString).as[T])
    } catch {
      case NonFatal(e) => Left(e)
    }
  }
}

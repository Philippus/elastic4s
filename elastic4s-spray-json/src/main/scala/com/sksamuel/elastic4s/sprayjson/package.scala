package com.sksamuel.elastic4s

import spray.json._

import scala.annotation.implicitNotFound
import scala.reflect.Manifest
import scala.util.control.NonFatal

package object sprayjson {

  @implicitNotFound("No RootJsonReader for type ${T} found. Bring an implicit RootJsonReader[T] instance in scope")
  implicit def format[T](implicit r: RootJsonReader[T],
                         mf: Manifest[T]): JsonFormat[T] = new JsonFormat[T] {
    override def fromJson(json: String): T = r.read(json.parseJson)
  }

  @implicitNotFound("No RootJsonWriter for type ${T} found. Bring an implicit RootJsonWriter[T] instance in scope")
  implicit def playJsonIndexable[T](implicit w: RootJsonWriter[T]) = new Indexable[T] {
    override def json(t: T): String = w.write(t).compactPrint
  }

  @implicitNotFound("No RootJsonReader for type ${T} found. Bring an implicit RootJsonReader[T] instance in scope")
  implicit def playJsonHitReader[T](implicit r: RootJsonReader[T]) = new HitReader[T] {
    override def read(hit: Hit): Either[Throwable, T] = try {
      Right(r.read(hit.sourceAsString.parseJson))
    } catch {
      case NonFatal(e) => Left(e)
    }
  }
}

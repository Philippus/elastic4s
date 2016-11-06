package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.searches.RichSearchHit
import play.api.libs.json.{Json, Reads, Writes}

import scala.annotation.implicitNotFound
import scala.util.control.NonFatal

package object playjson {

  @implicitNotFound("No Writes for type ${T} found. Bring an implicit Writes[T] instance in scope")
  implicit def playJsonIndexable[T](implicit w: Writes[T]) = new Indexable[T] {
    override def json(t: T): String = Json.stringify(Json.toJson(t)(w))
  }

  @implicitNotFound("No Reads for type ${T} found. Bring an implicit Reads[T] instance in scope")
  @deprecated("use HitReader which can be used for both get and search APIs", "5.0.0")
  implicit def playJsonHitAs[T](implicit r: Reads[T]) = new HitAs[T] {
    override def as(hit: RichSearchHit): T = Json.parse(hit.sourceAsString).as[T]
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

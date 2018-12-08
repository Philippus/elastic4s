package com.sksamuel.elastic4s

import play.api.libs.json.{Json, Reads, Writes}

import scala.annotation.implicitNotFound
import scala.util.Try

package object playjson {

  @implicitNotFound("No Writes for type ${T} found. Bring an implicit Writes[T] instance in scope")
  implicit def playJsonIndexable[T](implicit w: Writes[T]): Indexable[T] = new Indexable[T] {
    override def json(t: T): String = Json.stringify(Json.toJson(t)(w))
  }

  @implicitNotFound("No Reads for type ${T} found. Bring an implicit Reads[T] instance in scope")
  implicit def playJsonHitReader[T](implicit r: Reads[T]): HitReader[T] = new HitReader[T] {
    override def read(hit: Hit): Try[T] = Try {
      Json.parse(hit.sourceAsString).as[T]
    }
  }

  @implicitNotFound("No Reads for type ${T} found. Bring an implicit Reads[T] instance in scope")
  implicit def playJsonAggReader[T](implicit r: Reads[T]): AggReader[T] = new AggReader[T] {
    override def read(json: String): Try[T] = Try {
      Json.parse(json).as[T]
    }
  }
}

package com.sksamuel.elastic4s

import spray.json._

import scala.annotation.implicitNotFound
import scala.util.Try

package object sprayjson {

  @implicitNotFound("No RootJsonWriter for type ${T} found. Bring an implicit RootJsonWriter[T] instance in scope")
  implicit def sprayJsonIndexable[T](implicit w: RootJsonWriter[T]): Indexable[T] = new Indexable[T] {
    override def json(t: T): String = w.write(t).compactPrint
  }

  @implicitNotFound("No RootJsonReader for type ${T} found. Bring an implicit RootJsonReader[T] instance in scope")
  implicit def sprayJsonHitReader[T](implicit r: RootJsonReader[T]): HitReader[T] = new HitReader[T] {
    override def read(hit: Hit): Try[T] = Try {
      r.read(hit.sourceAsString.parseJson)
    }
  }

  @implicitNotFound("No RootJsonReader for type ${T} found. Bring an implicit RootJsonReader[T] instance in scope")
  implicit def sprayJsonAggReader[T](implicit r: RootJsonReader[T]): AggReader[T] = new AggReader[T] {
    override def read(json: String): Try[T] = Try {
      r.read(json.parseJson)
    }
  }
}

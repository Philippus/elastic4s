package com.sksamuel.elastic4s

import scala.annotation.implicitNotFound

@deprecated("use HitAs, this reader trait has a broken contravariance implementation", "1.6.1")
trait Reader[-U] {
  def read[T <: U : Manifest](json: String): T
}

@implicitNotFound(
  "No HitAs deserializer found for type ${T}." +
  "Try to implement an implicit HitAs instances for this type or use the elastic4s-shapeless contrib."
)
trait HitAs[T] {
  def as(hit: RichSearchHit): T
}
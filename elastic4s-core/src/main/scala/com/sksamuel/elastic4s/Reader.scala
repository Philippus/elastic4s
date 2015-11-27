package com.sksamuel.elastic4s

@deprecated("use HitReader, this reader trait has a broken contravariance implementation", "1.6.1")
trait Reader[-U] {
  def read[T <: U : Manifest](json: String): T
}

trait HitAs[T] {
  def as(hit: RichSearchHit): T
}
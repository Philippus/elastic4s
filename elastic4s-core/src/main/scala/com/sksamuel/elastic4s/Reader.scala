package com.sksamuel.elastic4s

@deprecated("use HitAs", "1.6.1")
trait Reader[-U] {
  def read[T <: U : Manifest](json: String): T
}

trait HitAs[-U] {
  def as[T <: U : Manifest](hit: RichSearchHit): T
}
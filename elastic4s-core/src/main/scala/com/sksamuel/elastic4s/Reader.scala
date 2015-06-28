package com.sksamuel.elastic4s

trait Reader[-U] {
  def read[T <: U : Manifest](json: String): T
}

trait HitAs[T] {
  def as(hit: RichSearchHit): T
}
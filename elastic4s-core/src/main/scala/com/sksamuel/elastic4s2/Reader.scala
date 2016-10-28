package com.sksamuel.elastic4s2

import com.sksamuel.elastic4s2.search.RichSearchHit

@deprecated("use HitAs, this reader trait has a broken contravariance implementation", "1.6.1")
trait Reader[-U] {
  def read[T <: U : Manifest](json: String): T
}

trait HitAs[T] {
  def as(hit: RichSearchHit): T
}

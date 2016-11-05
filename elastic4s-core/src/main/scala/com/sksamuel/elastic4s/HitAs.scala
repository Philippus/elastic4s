package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.searches.RichSearchHit

@deprecated("use Reader which supports unmarshalling from both get and search requests, and handles errors", "5.0.0")
trait HitAs[T] {
  def as(hit: RichSearchHit): T
}

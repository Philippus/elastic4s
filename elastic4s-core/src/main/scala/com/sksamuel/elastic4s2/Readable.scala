package com.sksamuel.elastic4s2

import com.sksamuel.elastic4s2.search.RichSearchHit

trait HitReader[T] {
  def read(hit: Hit): Either[Exception, T]
}

@deprecated("use Reader which supports unmarshalling from both get and search requests, and handles errors", "5.0.0")
trait HitAs[T] {
  def as(hit: RichSearchHit): T
}

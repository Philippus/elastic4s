package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.search.RichSearchHit

trait HitReader[T] {
  def read(hit: Hit): Either[Exception, T]
}

@deprecated("use Reader which supports unmarshalling from both get and search requests, and handles errors", "3.0.0")
trait HitAs[T] {
  def as(hit: RichSearchHit): T
}

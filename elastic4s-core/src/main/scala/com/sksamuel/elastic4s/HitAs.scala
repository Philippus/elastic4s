package com.sksamuel.elastic4s

trait HitAs[T] {
  def as(hit: RichSearchHit): T
}
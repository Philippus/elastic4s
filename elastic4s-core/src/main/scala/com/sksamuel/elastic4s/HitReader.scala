package com.sksamuel.elastic4s

trait HitReader[T] {
  def read(hit: Hit): Either[String, T]
}
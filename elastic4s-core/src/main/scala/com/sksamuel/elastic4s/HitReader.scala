package com.sksamuel.elastic4s

import scala.util.Try

trait HitReader[T] {
  def read(hit: Hit): Try[T]
}

trait AggReader[T] {
  def read(json: String): Try[T]
}

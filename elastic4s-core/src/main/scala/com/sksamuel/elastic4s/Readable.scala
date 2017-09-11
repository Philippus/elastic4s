package com.sksamuel.elastic4s

trait Readable[T] {
  def read(hit: Hit): Either[Throwable, T]
}

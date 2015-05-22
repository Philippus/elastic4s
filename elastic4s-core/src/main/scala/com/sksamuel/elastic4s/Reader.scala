package com.sksamuel.elastic4s

trait Reader[-U] {
  def read[T <: U : Manifest](json: String): T
}
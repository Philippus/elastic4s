package com.sksamuel.elastic4s.requests.synonyms

case class ListSynonymsSetRequest(from: Option[Int], size: Option[Int]) {
  def from(from: Int): ListSynonymsSetRequest = copy(from = Some(from))
  def size(size: Int): ListSynonymsSetRequest = copy(size = Some(size))
}

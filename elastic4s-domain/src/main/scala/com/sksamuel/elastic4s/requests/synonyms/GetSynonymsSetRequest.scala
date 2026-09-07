package com.sksamuel.elastic4s.requests.synonyms

case class GetSynonymsSetRequest(
    synonymsSet: String,
    from: Option[Int] = None,
    size: Option[Int] = None,
    searchAfter: Option[String] = None
) {
  def from(from: Int): GetSynonymsSetRequest                  = copy(from = Some(from))
  def size(size: Int): GetSynonymsSetRequest                  = copy(size = Some(size))
  def searchAfter(searchAfter: String): GetSynonymsSetRequest = copy(searchAfter = Some(searchAfter))
}

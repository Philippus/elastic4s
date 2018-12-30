package com.sksamuel.elastic4s.requests.termvectors

import com.sksamuel.elastic4s.{Index, IndexAndType}

trait TermVectorApi {
  def termVectors(index: Index, `type`: String, id: String): TermVectorsRequest =
    TermVectorsRequest(IndexAndType(index.name, `type`), id.toString)

  def multiTermVectors(first: TermVectorsRequest, rest: TermVectorsRequest*): MultiTermVectorsRequest = MultiTermVectorsRequest(first +: rest)
  def multiTermVectors(defs: Iterable[TermVectorsRequest]): MultiTermVectorsRequest = MultiTermVectorsRequest(defs.toSeq)
}

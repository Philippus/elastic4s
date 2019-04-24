package com.sksamuel.elastic4s.requests.termvectors

import com.sksamuel.elastic4s.Index

trait TermVectorApi {

  def termVectors(index: Index, id: String): TermVectorsRequest =
    TermVectorsRequest(index, id)

  def multiTermVectors(first: TermVectorsRequest, rest: TermVectorsRequest*): MultiTermVectorsRequest = MultiTermVectorsRequest(first +: rest)
  def multiTermVectors(defs: Iterable[TermVectorsRequest]): MultiTermVectorsRequest = MultiTermVectorsRequest(defs.toSeq)
}

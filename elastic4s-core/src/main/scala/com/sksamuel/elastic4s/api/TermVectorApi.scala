package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.requests.termvectors.{MultiTermVectorsRequest, TermVectorsRequest}

trait TermVectorApi {

  def termVectors(index: Index, id: String): TermVectorsRequest =
    TermVectorsRequest(index, id)

  def multiTermVectors(first: TermVectorsRequest, rest: TermVectorsRequest*): MultiTermVectorsRequest =
    MultiTermVectorsRequest(first +: rest)
  def multiTermVectors(defs: Iterable[TermVectorsRequest]): MultiTermVectorsRequest                   =
    MultiTermVectorsRequest(defs.toSeq)
}

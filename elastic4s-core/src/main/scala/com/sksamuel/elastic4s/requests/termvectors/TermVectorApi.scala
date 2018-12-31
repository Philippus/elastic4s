package com.sksamuel.elastic4s.requests.termvectors

import com.sksamuel.elastic4s.Index

trait TermVectorApi {

  def termVectors(index: Index, id: String): TermVectorsRequest =
    TermVectorsRequest(index, None, id.toString)

  @deprecated("using types in term vectors is deprecated because types are deprecated", "7.0")
  def termVectors(index: Index, `type`: String, id: String): TermVectorsRequest =
    TermVectorsRequest(index, Some(`type`), id.toString)

  def multiTermVectors(first: TermVectorsRequest, rest: TermVectorsRequest*): MultiTermVectorsRequest = MultiTermVectorsRequest(first +: rest)
  def multiTermVectors(defs: Iterable[TermVectorsRequest]): MultiTermVectorsRequest = MultiTermVectorsRequest(defs.toSeq)
}

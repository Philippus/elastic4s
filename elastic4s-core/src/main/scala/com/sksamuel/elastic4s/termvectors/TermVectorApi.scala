package com.sksamuel.elastic4s.termvectors

import com.sksamuel.elastic4s.IndexAndType

trait TermVectorApi {
  def termVectors(index: String, `type`: String, id: Any): TermVectorsDefinition =
    TermVectorsDefinition(IndexAndType(index, `type`), id.toString)
}

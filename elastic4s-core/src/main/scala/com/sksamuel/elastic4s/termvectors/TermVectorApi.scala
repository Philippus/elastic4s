package com.sksamuel.elastic4s.termvectors

import com.sksamuel.elastic4s.{Index, IndexAndType}

trait TermVectorApi {
  def termVectors(index: Index, `type`: String, id: String): TermVectorsDefinition =
    TermVectorsDefinition(IndexAndType(index.name, `type`), id.toString)
}

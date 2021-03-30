package com.sksamuel.elastic4s.requests.termvectors

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.DocumentRef

case class TermVectorsResponse(@JsonProperty("_index") index: String,
                               @JsonProperty("_type") `type`: String,
                               @JsonProperty("_id") id: String,
                               @JsonProperty("_version") version: Long,
                               found: Boolean,
                               took: Int,
                               @JsonProperty("term_vectors") termVectors: Map[String, TermVectors]) {
  def ref = DocumentRef(index, `type`, id)
}

case class FieldStatistics(@JsonProperty("sum_doc_freq") sumDocFreq: Long,
                           @JsonProperty("doc_count") docCount: Long,
                           @JsonProperty("sum_ttf") sumTtf: Long)

case class Terms(@JsonProperty("doc_freq") docFreq: Int,
                 @JsonProperty("ttf") ttf: Int,
                 @JsonProperty("score") score: Double,
                 @JsonProperty("term_freq") termFreq: Int,
                 tokens: Seq[Token])

case class Token(@JsonProperty("position") position: Int,
                 @JsonProperty("start_offset") startOffset: Int,
                 @JsonProperty("end_offset") endOffset: Int)

case class TermVectors(@JsonProperty("field_statistics") fieldStatistics: FieldStatistics, terms: Map[String, Terms])

case class MultiTermVectorsResponse(@JsonProperty("docs") docs: Seq[TermVectorsResponse])

package com.sksamuel.elastic4s.http.termvectors

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.termvectors.TermVectorsRequest
import org.apache.http.entity.ContentType

trait TermVectorHandlers {

  implicit object TermVectorHandler extends Handler[TermVectorsRequest, TermVectorsResponse] {

    override def requestHandler(request: TermVectorsRequest): ElasticRequest = {

      val endpoint = s"/${request.indexAndType.index}/${request.indexAndType.`type`}/${request.id}/_termvectors"

      val builder = XContentFactory.jsonBuilder()

      if (request.fields.nonEmpty)
        builder.array("fields", request.fields.toArray)

      request.termStatistics.foreach(builder.field("term_statistics", _))
      request.fieldStatistics.foreach(builder.field("field_statistics", _))
      request.payloads.foreach(builder.field("payloads", _))
      request.positions.foreach(builder.field("positions", _))
      request.offsets.foreach(builder.field("offsets", _))

      builder.startObject("filter")
      request.maxNumTerms.foreach(builder.field("max_num_terms", _))
      request.minTermFreq.foreach(builder.field("min_term_freq", _))
      request.maxTermFreq.foreach(builder.field("max_term_freq", _))
      request.minDocFreq.foreach(builder.field("min_doc_freq", _))
      request.maxDocFreq.foreach(builder.field("max_doc_freq", _))
      request.minWordLength.foreach(builder.field("min_word_length", _))
      request.maxWordLength.foreach(builder.field("max_word_length", _))
      builder.endObject()

      builder.endObject()

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.realtime.foreach(params.put("realtime", _))

      ElasticRequest("GET",
                     endpoint,
                     params.toMap,
                     HttpEntity(builder.string(), ContentType.APPLICATION_JSON.getMimeType))
    }
  }
}

case class TermVectorsResponse(@JsonProperty("_index") index: String,
                               @JsonProperty("_type") `type`: String,
                               @JsonProperty("_id") id: String,
                               @JsonProperty("_version") version: Long,
                               found: Boolean,
                               took: Int,
                               @JsonProperty("term_vectors") termVectors: Map[String, TermVectors]) {
  def ref = DocumentRef(index, `type`, id)
}

case class FieldStatistics(@JsonProperty("sum_doc_freq") sumDocFreq: Int,
                           @JsonProperty("doc_count") docCount: Int,
                           @JsonProperty("sum_ttf") sumTtf: Int)

case class Terms(@JsonProperty("doc_freq") docFreq: Int,
                 @JsonProperty("ttf") ttf: Int,
                 @JsonProperty("score") score: Double,
                 @JsonProperty("term_freq") termFreq: Int,
                 tokens: Seq[Token])

case class Token(@JsonProperty("position") position: Int,
                 @JsonProperty("start_offset") startOffset: Int,
                 @JsonProperty("end_offset") endOffset: Int)

case class TermVectors(@JsonProperty("field_statistics") fieldStatistics: FieldStatistics, terms: Map[String, Terms])

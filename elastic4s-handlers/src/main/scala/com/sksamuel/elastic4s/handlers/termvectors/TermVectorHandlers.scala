package com.sksamuel.elastic4s.handlers.termvectors

import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.requests.termvectors.{MultiTermVectorsRequest, MultiTermVectorsResponse, TermVectorsRequest, TermVectorsResponse}
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity}

trait TermVectorHandlers {

  implicit object TermVectorHandler extends Handler[TermVectorsRequest, TermVectorsResponse] {

    override def build(request: TermVectorsRequest): ElasticRequest = {

      val endpoint = s"/${request.index.name}/_termvectors/${request.id}"

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
        HttpEntity(builder.string(), "application/json"))
    }
  }

  implicit object MultiTermVectorsHttpExecutable extends Handler[MultiTermVectorsRequest, MultiTermVectorsResponse] {

    override def build(request: MultiTermVectorsRequest): ElasticRequest = {

      val endpoint = s"/_mtermvectors"

      val builder = XContentFactory.jsonBuilder()
      builder.startArray("docs")

      // Workaround for bug where separator is not added with rawValues
      val arrayBody = request.termVectorsRequests.map { r =>
        val builder = XContentFactory.jsonBuilder()

        builder.field("_index", r.index.name)
        builder.field("_id", r.id)

        if (r.fields.nonEmpty)
          builder.array("fields", r.fields.toArray)

        r.termStatistics.foreach(builder.field("term_statistics", _))
        r.fieldStatistics.foreach(builder.field("field_statistics", _))
        r.payloads.foreach(builder.field("payloads", _))
        r.positions.foreach(builder.field("positions", _))
        r.offsets.foreach(builder.field("offsets", _))

        builder.startObject("filter")
        r.maxNumTerms.foreach(builder.field("max_num_terms", _))
        r.minTermFreq.foreach(builder.field("min_term_freq", _))
        r.maxTermFreq.foreach(builder.field("max_term_freq", _))
        r.minDocFreq.foreach(builder.field("min_doc_freq", _))
        r.maxDocFreq.foreach(builder.field("max_doc_freq", _))
        r.minWordLength.foreach(builder.field("min_word_length", _))
        r.maxWordLength.foreach(builder.field("max_word_length", _))
        builder.endObject()

        builder.endObject()

        builder.string()
      }.mkString(",")
      builder.rawValue(arrayBody)

      builder.endArray()
      builder.endObject()

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.realtime.foreach(params.put("realtime", _))

      ElasticRequest("POST",
        endpoint,
        params.toMap,
        HttpEntity(builder.string(), "application/json"))
    }
  }
}

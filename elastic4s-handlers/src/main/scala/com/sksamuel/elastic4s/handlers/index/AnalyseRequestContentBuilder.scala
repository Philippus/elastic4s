package com.sksamuel.elastic4s.handlers.index

import com.sksamuel.elastic4s.SimpleFieldValue
import com.sksamuel.elastic4s.json.{XContentFactory, XContentFieldValueWriter}
import com.sksamuel.elastic4s.requests.indexes.analyze.AnalyzeRequest

object AnalyseRequestContentBuilder {

  def apply(request: AnalyzeRequest): String = {

    val source = XContentFactory.jsonBuilder()

    def simpleFieldValue(name: String, value: Any): Unit = {
      XContentFieldValueWriter(source, SimpleFieldValue(name, value))
    }

    simpleFieldValue("text", request.text)
    request.analyzer.foreach(simpleFieldValue("analyzer", _))

    if (request.explain) {
      simpleFieldValue("explain", true)
    }

    request.tokenizer.foreach(simpleFieldValue("tokenizer", _))

    if (request.filters.nonEmpty || request.rawFiltersFromAnalyzer.nonEmpty) {
      source.startArray("filter")
      source.rawValue(request.filters.map("\"" + _ + "\"").mkString(","))
      request.rawFiltersFromAnalyzer.map(_.build)
        .foreach(source.rawValue)
      source.endArray()
    }

    if (request.charFilters.nonEmpty) {
      simpleFieldValue("char_filter", request.charFilters)
    }

    request.normalizer.foreach(simpleFieldValue("normalizer", _))
    request.field.foreach(simpleFieldValue("field", _))

    if (request.attributes.nonEmpty) {
      simpleFieldValue("attributes", request.attributes)
    }

    source.endObject().string
  }

}

package com.sksamuel.elastic4s.requests.analysis

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

case class Analysis(analyzers: List[Analyzer],
                    tokenizers: List[Tokenizer] = Nil,
                    tokenFilters: List[TokenFilter] = Nil,
                    charFilters: List[CharFilter] = Nil,
                    normalizers: List[Normalizer] = Nil)

object AnalysisBuilder extends Builder[Analysis] {
  override def build(a: Analysis): XContentBuilder = {

    val b = XContentFactory.jsonBuilder()

    if (a.analyzers.nonEmpty) {
      b.startObject("analyzer")
      a.analyzers.foreach { analyzer => b.rawField(analyzer.name, analyzer.build) }
      b.endObject()
    }

    if (a.normalizers.nonEmpty) {
      b.startObject("normalizer")
      a.normalizers.foreach { normalizer => b.rawField(normalizer.name, normalizer.build) }
      b.endObject()
    }

    if (a.tokenizers.nonEmpty) {
      b.startObject("tokenizer")
      a.tokenizers.foreach { tokenizer => b.rawField(tokenizer.name, tokenizer.build) }
      b.endObject()
    }

    if (a.charFilters.nonEmpty) {
      b.startObject("char_filter")
      a.charFilters.foreach { filter => b.rawField(filter.name, filter.build) }
      b.endObject()
    }

    if (a.tokenFilters.nonEmpty) {
      b.startObject("filter")
      a.tokenFilters.foreach { filter => b.rawField(filter.name, filter.build) }
      b.endObject()
    }

    b.endObject()
  }
}

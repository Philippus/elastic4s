package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.XContentBuilder

object AnalysisBuilderFn {

  def build(ad: AnalysisDefinition, source: XContentBuilder) {
    source.startObject("analysis")

    val charFilterDefinitions = ad.charFilterDefinitions
    if (charFilterDefinitions.nonEmpty) {
      source.startObject("char_filter")
      charFilterDefinitions.foreach { filter =>
        source.startObject(filter.name)
        source.field("type", filter.filterType)
        filter.build(source)
        source.endObject()
      }
      source.endObject()
    }

    source.startObject("analyzer")
    ad.analyzers.foreach(_.buildWithName(source))
    source.endObject()

    val normalizers = ad.normalizers
    if (normalizers.nonEmpty) {
      source.startObject("normalizer")
      normalizers.foreach(_.buildWithName(source))
      source.endObject()
    }

    val tokenizers = ad.tokenizers
    if (tokenizers.nonEmpty) {
      source.startObject("tokenizer")
      tokenizers.foreach(tokenizer => {
        source.startObject(tokenizer.name)
        tokenizer.build(source)
        source.endObject()
      })
      source.endObject()
    }

    val tokenFilterDefinitions = ad.tokenFilterDefinitions
    if (tokenFilterDefinitions.nonEmpty) {
      source.startObject("filter")
      tokenFilterDefinitions.foreach(filter => {
        source.startObject(filter.name)
        source.field("type", filter.filterType)
        filter.build(source)
        source.endObject()
      })
      source.endObject()
    }

    source.endObject()
  }
}

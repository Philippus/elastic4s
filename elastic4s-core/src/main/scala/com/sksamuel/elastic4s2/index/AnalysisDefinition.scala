package com.sksamuel.elastic4s2.index

import com.sksamuel.elastic4s2.analyzers.{AnalyzerDefinition, CharFilterDefinition, CustomAnalyzerDefinition, TokenFilterDefinition, Tokenizer}
import org.elasticsearch.common.xcontent.XContentBuilder

case class AnalysisDefinition(analyzers: Iterable[AnalyzerDefinition]) {

  def tokenizers: Iterable[Tokenizer] =
    analyzers.collect {
      case custom: CustomAnalyzerDefinition => custom
    }.map(_.tokenizer).filter(_.customized)

  def tokenFilterDefinitions: Iterable[TokenFilterDefinition] =
    analyzers.collect {
      case custom: CustomAnalyzerDefinition => custom
    }.flatMap(_.filters).collect {
      case token: TokenFilterDefinition => token
    }

  def charFilterDefinitions: Iterable[CharFilterDefinition] =
    analyzers.collect {
      case custom: CustomAnalyzerDefinition => custom
    }.flatMap(_.filters).collect {
      case char: CharFilterDefinition => char
    }

  private[elastic4s2] def build(source: XContentBuilder) {
    source.startObject("analysis")

    val charFilterDefinitions = this.charFilterDefinitions
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
    analyzers.foreach(analyzer => {
      source.startObject(analyzer.name)
      analyzer.build(source)
      source.endObject()
    })
    source.endObject()

    val tokenizers = this.tokenizers
    if (tokenizers.nonEmpty) {
      source.startObject("tokenizer")
      tokenizers.foreach(tokenizer => {
        source.startObject(tokenizer.name)
        tokenizer.build(source)
        source.endObject()
      })
      source.endObject()
    }

    val tokenFilterDefinitions = this.tokenFilterDefinitions
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

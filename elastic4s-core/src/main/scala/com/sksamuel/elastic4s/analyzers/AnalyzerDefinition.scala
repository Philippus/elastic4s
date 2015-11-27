package com.sksamuel.elastic4s.analyzers

import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

// Base class for analyzers that have custom parameters set.
abstract class AnalyzerDefinition(val name: String) {
  def build(source: XContentBuilder): Unit
  def json: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder
    builder.startObject()
    build(builder)
    builder.endObject()
    builder
  }
}

case class StopAnalyzerDefinition(override val name: String,
                                  stopwords: Iterable[String] = Nil) extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "stop")
    source.field("stopwords", stopwords.toArray[String]: _*)
  }

  def stopwords(stopwords: Iterable[String]): StopAnalyzerDefinition = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): StopAnalyzerDefinition = copy(stopwords = stopwords +: rest)
}

case class StandardAnalyzerDefinition(override val name: String,
                                      stopwords: Iterable[String] = Nil,
                                      maxTokenLength: Int = 255) extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "standard")
    source.field("stopwords", stopwords.toArray[String]: _*)
    source.field("max_token_length", maxTokenLength)
  }

  def stopwords(stopwords: Iterable[String]): StandardAnalyzerDefinition = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): StandardAnalyzerDefinition = copy(stopwords = stopwords +: rest)
  def maxTokenLength(maxTokenLength: Int): StandardAnalyzerDefinition = copy(maxTokenLength = maxTokenLength)
}

case class PatternAnalyzerDefinition(override val name: String,
                                     regex: String,
                                     lowercase: Boolean = true) extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "pattern")
    source.field("lowercase", lowercase)
    source.field("pattern", regex)
  }

  def lowercase(lowercase: Boolean): PatternAnalyzerDefinition = copy(lowercase = lowercase)
}

case class SnowballAnalyzerDefinition(override val name: String,
                                      lang: String = "English",
                                      stopwords: Iterable[String] = Nil) extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "snowball")
    source.field("language", lang)
    if (stopwords.nonEmpty)
      source.field("stopwords", stopwords.toArray[String]: _*)
  }

  def language(lang: String): SnowballAnalyzerDefinition = copy(lang = lang)
  def stopwords(stopwords: Iterable[String]): SnowballAnalyzerDefinition = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): SnowballAnalyzerDefinition = copy(stopwords = stopwords +: rest)
}

case class CustomAnalyzerDefinition(override val name: String,
                                    tokenizer: Tokenizer,
                                    filters: Seq[AnalyzerFilter] = Nil) extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "custom")
    source.field("tokenizer", tokenizer.name)
    val tokenFilters = filters.collect { case token: TokenFilter => token }
    val charFilters = filters.collect { case char: CharFilter => char }
    if (tokenFilters.nonEmpty) {
      source.field("filter", tokenFilters.map(_.name).toArray: _*)
    }
    if (charFilters.nonEmpty) {
      source.field("char_filter", charFilters.map(_.name).toArray: _*)
    }
  }

  def filters(filters: Seq[AnalyzerFilter]): CustomAnalyzerDefinition = copy(filters = filters)
  def addFilter(filter: AnalyzerFilter): CustomAnalyzerDefinition = copy(filters = filters :+ filter)
}

object CustomAnalyzerDefinition {
  def apply(name: String,
            tokenizer: Tokenizer,
            first: AnalyzerFilter,
            rest: AnalyzerFilter*): CustomAnalyzerDefinition = {
    CustomAnalyzerDefinition(name, tokenizer, first +: rest)
  }
}

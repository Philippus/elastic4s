package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

// Base class for analyzers that have custom parameters set.
abstract class AnalyzerDefinition(val name: String) {

  def buildWithName(source: XContentBuilder): Unit = {
    source.startObject(name)
    build(source)
    source.endObject()
  }

  def buildWithName(): XContentBuilder = {
    val xc = XContentFactory.jsonBuilder()
    buildWithName(xc)
    xc.endObject()
  }

  def build(): XContentBuilder = {
    val xc = XContentFactory.jsonBuilder()
    build(xc)
    xc.endObject()
  }

  def build(source: XContentBuilder): Unit

  def json: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder
    build(builder)
    builder.endObject()
  }
}

case class StopAnalyzerDefinition(override val name: String, stopwords: Iterable[String] = Nil)
    extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "stop")
    source.array("stopwords", stopwords.toArray)
  }

  def stopwords(stopwords: Iterable[String]): StopAnalyzerDefinition      = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): StopAnalyzerDefinition = copy(stopwords = stopwords +: rest)
}

case class StandardAnalyzerDefinition(override val name: String,
                                      stopwords: Iterable[String] = Nil,
                                      maxTokenLength: Int = 255)
    extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "standard")
    source.array("stopwords", stopwords.toArray)
    source.field("max_token_length", maxTokenLength)
  }

  def stopwords(stopwords: Iterable[String]): StandardAnalyzerDefinition      = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): StandardAnalyzerDefinition = copy(stopwords = stopwords +: rest)
  def maxTokenLength(maxTokenLength: Int): StandardAnalyzerDefinition         = copy(maxTokenLength = maxTokenLength)
}

case class PatternAnalyzerDefinition(override val name: String, regex: String, lowercase: Boolean = true)
    extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "pattern")
    source.field("lowercase", lowercase)
    source.field("pattern", regex)
  }

  def lowercase(lowercase: Boolean): PatternAnalyzerDefinition = copy(lowercase = lowercase)
}

case class SnowballAnalyzerDefinition(override val name: String,
                                      lang: String = "English",
                                      stopwords: Iterable[String] = Nil)
    extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "snowball")
    source.field("language", lang)
    if (stopwords.nonEmpty)
      source.array("stopwords", stopwords.toArray)
  }

  def language(lang: String): SnowballAnalyzerDefinition                      = copy(lang = lang)
  def stopwords(stopwords: Iterable[String]): SnowballAnalyzerDefinition      = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): SnowballAnalyzerDefinition = copy(stopwords = stopwords +: rest)
}

case class CustomAnalyzerDefinition(override val name: String, tokenizer: Tokenizer, filters: Seq[AnalyzerFilter] = Nil)
    extends AnalyzerDefinition(name) {

  def build(source: XContentBuilder): Unit = {
    source.field("type", "custom")
    source.field("tokenizer", tokenizer.name)
    val tokenFilters = filters.collect { case token: TokenFilter => token }
    val charFilters  = filters.collect { case char: CharFilter   => char }
    if (tokenFilters.nonEmpty)
      source.array("filter", tokenFilters.map(_.name).toArray)
    if (charFilters.nonEmpty)
      source.array("char_filter", charFilters.map(_.name).toArray)
  }

  def filters(filters: Seq[AnalyzerFilter]): CustomAnalyzerDefinition = copy(filters = filters)
  def addFilter(filter: AnalyzerFilter): CustomAnalyzerDefinition     = copy(filters = filters :+ filter)
}

object CustomAnalyzerDefinition {
  def apply(name: String,
            tokenizer: Tokenizer,
            first: AnalyzerFilter,
            rest: AnalyzerFilter*): CustomAnalyzerDefinition =
    CustomAnalyzerDefinition(name, tokenizer, first +: rest)
}

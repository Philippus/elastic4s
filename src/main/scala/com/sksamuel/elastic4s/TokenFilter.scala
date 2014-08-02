package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.XContentBuilder

sealed trait TokenFilter extends AnalyzerFilter

sealed trait TokenFilterDefinition extends TokenFilter with AnalyzerFilterDefinition

case object ReverseTokenFilter extends TokenFilter {
  val name = "reverse"
}

case object TrimTokenFilter extends TokenFilter {
  val name = "trim"
}

case object StandardTokenFilter extends TokenFilter {
  val name = "standard"
}

case object AsciiFoldingTokenFilter extends TokenFilter {
  val name = "asciifolding"
}

case object LowercaseTokenFilter extends TokenFilter {
  val name = "lowercase"
}

case object KStemTokenFilter extends TokenFilter {
  val name = "kstem"
}

case object PorterStemTokenFilter extends TokenFilter {
  val name = "porterStem"
}

case object UniqueTokenFilter extends TokenFilter {
  val name = "unique"
}

case class TruncateTokenFilter(name: String, length: Int = 10)
    extends TokenFilterDefinition {

  val filterType = "truncate"

  override def build(source: XContentBuilder): Unit = {
    source.field("length", length)
  }
}

case class LengthTokenFilter(name: String, min: Int = 0, max: Int = Integer.MAX_VALUE)
    extends TokenFilterDefinition {

  val filterType = "length"

  override def build(source: XContentBuilder): Unit = {
    if (min > 0) source.field("min", min)
    if (max < Integer.MAX_VALUE) source.field("max", max)
  }
}

case class UniqueTokenFilter(name: String, onlyOnSamePosition: Boolean = false)
    extends TokenFilterDefinition {

  val filterType = "unique"

  override def build(source: XContentBuilder): Unit = {
    source.field("only_on_same_position", onlyOnSamePosition)
  }
}

case class KeywordMarkerTokenFilter(name: String,
                                    keywords: Iterable[String] = Nil,
                                    ignoreCase: Boolean = false)
    extends TokenFilterDefinition {

  val filterType = "keyword_marker"

  override def build(source: XContentBuilder): Unit = {
    if (keywords.size > 0) source.field("keywords", keywords.toArray[String]: _*)
    if (ignoreCase) source.field("ignore_case", ignoreCase)
  }
}

case class ElisionTokenFilter(name: String, articles: Iterable[String])
    extends TokenFilterDefinition {

  val filterType = "elision"

  override def build(source: XContentBuilder): Unit = {
    source.field("articles", articles.toArray[String]: _*)
  }
}

case class LimitTokenFilter(name: String,
                            maxTokenCount: Int = 1,
                            consumeAllTokens: Boolean = false)
    extends TokenFilterDefinition {

  val filterType = "stop"

  override def build(source: XContentBuilder): Unit = {
    if (maxTokenCount > 1) source.field("max_token_count", maxTokenCount)
    if (consumeAllTokens) source.field("consume_all_tokens", consumeAllTokens)
  }
}

case class StopTokenFilter(name: String,
                           stopwords: Iterable[String] = Nil,
                           enablePositionIncrements: Boolean = true,
                           ignoreCase: Boolean = false)
    extends TokenFilterDefinition {

  val filterType = "stop"

  override def build(source: XContentBuilder): Unit = {
    source.field("stopwords", stopwords.toArray[String]: _*)
    source.field("enable_position_increments", enablePositionIncrements)
    if (ignoreCase) source.field("ignore_case", ignoreCase)
  }
}

case class PatternCaptureTokenFilter(name: String,
                                     patterns: Iterable[String],
                                     preserveOriginal: Boolean = true)
    extends TokenFilterDefinition {

  val filterType = "pattern_capture"

  override def build(source: XContentBuilder): Unit = {
    source.field("patterns", patterns.toArray[String]: _*)
    source.field("preserve_original", preserveOriginal)
  }
}

case class PatternReplaceTokenFilter(name: String, pattern: String, replacement: String)
    extends TokenFilterDefinition {

  val filterType = "pattern_replace"

  override def build(source: XContentBuilder): Unit = {
    source.field("pattern", pattern)
    source.field("replacement", replacement)
  }
}

case class CommongGramsTokenFilter(name: String,
                                   commonWords: Iterable[String],
                                   ignoreCase: Boolean = false,
                                   queryMode: Boolean = false)
    extends TokenFilterDefinition {

  val filterType = "common_grams"

  override def build(source: XContentBuilder): Unit = {
    source.field("common_words", commonWords.toArray[String]: _*)
    source.field("ignore_case", ignoreCase)
    source.field("query_mode", queryMode)
  }
}

case class EdgeNGramTokenFilter(name: String, minGram: Int = 1, maxGram: Int = 2)
    extends TokenFilterDefinition {

  val filterType = "edgeNGram"

  override def build(source: XContentBuilder): Unit = {
    source.field("min_gram", minGram)
    source.field("max_gram", maxGram)
  }
}

case class NGramTokenFilter(name: String, minGram: Int = 1, maxGram: Int = 2)
    extends TokenFilterDefinition {

  val filterType = "nGram"

  override def build(source: XContentBuilder): Unit = {
    source.field("min_gram", minGram)
    source.field("max_gram", maxGram)
  }
}

case class SnowballTokenFilter(name: String, language: String = "English")
    extends TokenFilterDefinition {

  val filterType = "snowball"

  override def build(source: XContentBuilder): Unit = {
    source.field("language", language)
  }
}

case class StemmerTokenFilter(name: String, lang: String)
    extends TokenFilterDefinition {

  val filterType = "stemmer"

  override def build(source: XContentBuilder): Unit = {
    source.field("name", lang)
  }
}

case class StemmerOverrideTokenFilter(name: String, rules: Array[String])
    extends TokenFilterDefinition {

  val filterType = "stemmer_override"

  override def build(source: XContentBuilder): Unit = {
    source.field("rules", rules: _*)
  }
}

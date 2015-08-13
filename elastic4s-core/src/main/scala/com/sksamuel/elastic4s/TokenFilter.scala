package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.XContentBuilder

trait TokenFilter extends AnalyzerFilter

trait TokenFilterDefinition extends TokenFilter with AnalyzerFilterDefinition

case object ReverseTokenFilter extends TokenFilter {
  val name = "reverse"
}

case object ApostropheTokenFilter extends TokenFilter {
  val name = "apostrophe"
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

case class SynonymTokenFilter(name: String,
                         path: Option[String],
                         synonyms: Set[String],
                         ignoreCase: Option[Boolean],
                         format: Option[String],
                         expand: Option[Boolean],
                         tokenizer: Option[Tokenizer])
    extends TokenFilterDefinition {

  val filterType = "synonym"

  override def build(source: XContentBuilder): Unit = {
    path.foreach(source.field("synonyms_path", _))
    synonyms.foreach(source.field("synonyms", _))
    format.foreach(source.field("format", _))
    ignoreCase.foreach(source.field("ignore_case", _))
    expand.foreach(source.field("expand", _))
    tokenizer.foreach(t => source.field("tokenizer", t.name))
  }
}

object SynonymTokenFilter {
  @deprecated("for backwards compatibility, move to expanded method", "1.5.6")
  def apply(name: String,
            path: String): SynonymTokenFilter = {
    SynonymTokenFilter(name, Option(path), Set.empty, None, None, None, None)
  }
  @deprecated("for backwards compatibility, move to expanded method", "1.5.6")
  def apply(name: String,
            path: String,
            ignoreCase: Boolean): SynonymTokenFilter = {
    SynonymTokenFilter(name, Option(path), Set.empty, Some(ignoreCase), None, None, None)
  }
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
                           enablePositionIncrements: Boolean = false, // ignored now as of 1.4.0
                           ignoreCase: Boolean = false)
    extends TokenFilterDefinition {

  val filterType = "stop"

  override def build(source: XContentBuilder): Unit = {
    source.field("stopwords", stopwords.toArray[String]: _*)
    if (enablePositionIncrements) source.field("enable_position_increments", enablePositionIncrements)
    if (ignoreCase) source.field("ignore_case", ignoreCase)
  }
}

case class NamedStopTokenFilter(name: String, stopwords: String,
                                enablePositionIncrements: Boolean = true,
                                ignoreCase: Boolean = false)
    extends TokenFilterDefinition {

  val filterType = "stop"

  override def build(source: XContentBuilder): Unit = {
    source.field("stopwords", stopwords)
    source.field("enable_position_increments", enablePositionIncrements)
    if (ignoreCase) source.field("ignore_case", ignoreCase)
  }
}

object NamedStopTokenFilter {
  val Arabic = "_arabic_"
  val Armenian = "_armenian_"
  val Basque = "_basque_"
  val Brazilian = "_brazilian_"
  val Bulgarian = "_bulgarian_"
  val Catalan = "_catalan_"
  val Czech = "_czech_"
  val Danish = "_danish_"
  val Dutch = "_dutch_"
  val English = "_english_"
  val Finnish = "_finnish_"
  val French = "_french_"
  val Galician = "_galician_"
  val German = "_german_"
  val Greek = "_greek_"
  val Hindi = "_hindi_"
  val Hungarian = "_hungarian_"
  val Indonesian = "_indonesian_"
  val Italian = "_italian_"
  val Norwegian = "_norwegian_"
  val Persian = "_persian_"
  val Portuguese = "_portuguese_"
  val Romanian = "_romanian_"
  val Russian = "_russian_"
  val Spanish = "_spanish_"
  val Swedish = "_swedish_"
  val Turkish = "_turkish_"
}

case class StopTokenFilterPath(name: String,
                               stopwords_path: String,
                               enablePositionIncrements: Boolean = false,
                               ignoreCase: Boolean = false)
  extends TokenFilterDefinition {

  val filterType = "stop"

  override def build(source: XContentBuilder): Unit = {
    source.field("stopwords_path", stopwords_path)
    if (enablePositionIncrements) source.field("enable_position_increments", enablePositionIncrements)
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

  def patterns(patterns: Iterable[String]): PatternCaptureTokenFilter = copy(patterns = patterns)
  def preserveOriginal(preserveOriginal: Boolean): PatternCaptureTokenFilter = copy(preserveOriginal = preserveOriginal)
}

case class PatternReplaceTokenFilter(name: String, pattern: String, replacement: String)
    extends TokenFilterDefinition {

  val filterType = "pattern_replace"

  override def build(source: XContentBuilder): Unit = {
    source.field("pattern", pattern)
    source.field("replacement", replacement)
  }

  def pattern(p: String): PatternReplaceTokenFilter = copy(pattern = p)
  def replacement(r: String): PatternReplaceTokenFilter = copy(replacement = r)
}

case class CommonGramsTokenFilter(name: String,
                                  commonWords: Iterable[String] = Set.empty,
                                  ignoreCase: Boolean = false,
                                  queryMode: Boolean = false)
    extends TokenFilterDefinition {

  val filterType = "common_grams"

  override def build(source: XContentBuilder): Unit = {
    source.field("common_words", commonWords.toArray[String]: _*)
    source.field("ignore_case", ignoreCase)
    source.field("query_mode", queryMode)
  }

  def commonWords(words: Iterable[String]): CommonGramsTokenFilter = copy(commonWords = words)
  def ignoreCase(ignoreCase: Boolean): CommonGramsTokenFilter = copy(ignoreCase = ignoreCase)
  def queryMode(queryMode: Boolean): CommonGramsTokenFilter = copy(queryMode = queryMode)
}

case class EdgeNGramTokenFilter(name: String, minGram: Int = 1, maxGram: Int = 2, side: String = "front")
    extends TokenFilterDefinition {

  val filterType = "edgeNGram"

  override def build(source: XContentBuilder): Unit = {
    source.field("min_gram", minGram)
    source.field("max_gram", maxGram)
    source.field("side", side)
  }

  def minGram(min: Int): EdgeNGramTokenFilter = copy(minGram = min)
  def maxGram(max: Int): EdgeNGramTokenFilter = copy(maxGram = max)
  def side(side: String): EdgeNGramTokenFilter = copy(side = side)
}

case class NGramTokenFilter(name: String, minGram: Int = 1, maxGram: Int = 2)
    extends TokenFilterDefinition {

  val filterType = "nGram"

  override def build(source: XContentBuilder): Unit = {
    source.field("min_gram", minGram)
    source.field("max_gram", maxGram)
  }

  def minGram(min: Int): NGramTokenFilter = copy(minGram = min)
  def maxGram(max: Int): NGramTokenFilter = copy(maxGram = max)
}

case class SnowballTokenFilter(name: String, language: String = "English")
    extends TokenFilterDefinition {

  val filterType = "snowball"

  override def build(source: XContentBuilder): Unit = {
    source.field("language", language)
  }

  def lang(l: String): SnowballTokenFilter = copy(language = l)
}

case class StemmerTokenFilter(name: String, lang: String = "English")
    extends TokenFilterDefinition {

  val filterType = "stemmer"

  override def build(source: XContentBuilder): Unit = {
    source.field("name", lang)
  }

  def lang(l: String): StemmerTokenFilter = copy(lang = l)
}

case class StemmerOverrideTokenFilter(name: String, rules: Array[String])
    extends TokenFilterDefinition {

  val filterType = "stemmer_override"

  override def build(source: XContentBuilder): Unit = {
    source.field("rules", rules: _*)
  }

  def rules(rules: Array[String]): StemmerOverrideTokenFilter = copy(rules = rules)
}

case class WordDelimiterTokenFilter(name: String,
                                    generateWordParts: Boolean = true,
                                    generateNumberParts: Boolean = true,
                                    catenateWords: Boolean = false,
                                    catenateNumbers: Boolean = false,
                                    catenateAll: Boolean = false,
                                    splitOnCaseChange: Boolean = true,
                                    preserveOriginal: Boolean = false,
                                    splitOnNumerics: Boolean = true,
                                    stemEnglishPossesive: Boolean = true)
    extends TokenFilterDefinition {

  val filterType = "word_delimiter"

  override def build(source: XContentBuilder): Unit = {
    source.field("generate_word_parts", generateWordParts)
    source.field("generate_number_parts", generateNumberParts)
    source.field("catenate_words", catenateWords)
    source.field("catenate_numbers", catenateNumbers)
    source.field("catenate_all", catenateAll)
    source.field("split_on_case_change", splitOnCaseChange)
    source.field("preserve_original", preserveOriginal)
    source.field("split_on_numerics", splitOnNumerics)
    source.field("stem_english_possessive", stemEnglishPossesive)
  }
}

case class ShingleTokenFilter(name: String,
                              max_shingle_size: Int = 2,
                              min_shingle_size: Int = 2,
                              output_unigrams: Boolean = true,
                              output_unigrams_if_no_shingles: Boolean = false,
                              token_separator: String = " ",
                              filler_token: String = "_")

    extends TokenFilterDefinition {

  val filterType = "shingle"

  override def build(source: XContentBuilder): Unit = {
    source.field("max_shingle_size", max_shingle_size)
    source.field("min_shingle_size", min_shingle_size)
    source.field("output_unigrams", output_unigrams)
    source.field("output_unigrams_if_no_shingles", output_unigrams_if_no_shingles)
    source.field("token_separator", token_separator)
    source.field("filler_token", filler_token)
  }

  def maxShingleSize(max: Int): ShingleTokenFilter = copy(max_shingle_size = max)
  def minShingleSize(min: Int): ShingleTokenFilter = copy(min_shingle_size = min)
  def outputUnigrams(b: Boolean): ShingleTokenFilter = copy(output_unigrams = b)
  def outputUnigramsIfNoShingles(b: Boolean): ShingleTokenFilter = copy(output_unigrams_if_no_shingles = b)
  def tokenSeperator(sep: String): ShingleTokenFilter = copy(token_separator = sep)
  def fillerToken(filler: String): ShingleTokenFilter = copy(filler_token = filler)
}

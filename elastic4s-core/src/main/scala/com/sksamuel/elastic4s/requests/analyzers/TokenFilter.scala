package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.XContentBuilder
import com.sksamuel.exts.OptionImplicits._

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

case object UppercaseTokenFilter extends TokenFilter {
  val name = "uppercase"
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
                              path: Option[String] = None,
                              synonyms: Set[String] = Set.empty,
                              ignoreCase: Option[Boolean] = None,
                              format: Option[String] = None,
                              expand: Option[Boolean] = None,
                              tokenizer: Option[Tokenizer] = None)
    extends TokenFilterDefinition {

  require(path.isDefined || synonyms.nonEmpty, "synonym requires either `synonyms` or `synonyms_path` to be configured")

  val filterType = "synonym"

  override def build(source: XContentBuilder): Unit = {
    path.foreach(source.field("synonyms_path", _))
    source.array("synonyms", synonyms.toArray)
    format.foreach(source.field("format", _))
    ignoreCase.foreach(source.field("ignore_case", _))
    expand.foreach(source.field("expand", _))
    tokenizer.foreach(t => source.field("tokenizer", t.name))
  }

  def path(path: String): SynonymTokenFilter                   = copy(path = Some(path))
  def synonyms(synonyms: Iterable[String]): SynonymTokenFilter = copy(synonyms = synonyms.toSet)
  def tokenizer(tokenizer: Tokenizer): SynonymTokenFilter      = copy(tokenizer = Some(tokenizer))
  def format(format: String): SynonymTokenFilter               = copy(format = Some(format))
  def ignoreCase(ignoreCase: Boolean): SynonymTokenFilter      = copy(ignoreCase = Some(ignoreCase))
  def expand(expand: Boolean): SynonymTokenFilter              = copy(expand = Some(expand))
}

case class TruncateTokenFilter(name: String, length: Option[Int] = None) extends TokenFilterDefinition {

  val filterType = "truncate"

  override def build(source: XContentBuilder): Unit =
    length.foreach(source.field("length", _))

  def length(length: Int): TruncateTokenFilter = copy(length = length.some)
}

case class LengthTokenFilter(name: String, min: Option[Int] = None, max: Option[Int] = None)
    extends TokenFilterDefinition {

  val filterType = "length"

  override def build(source: XContentBuilder): Unit = {
    min.foreach(source.field("min", _))
    max.foreach(source.field("max", _))
  }

  def min(min: Int): LengthTokenFilter = copy(min = min.some)
  def max(max: Int): LengthTokenFilter = copy(max = max.some)
}

case class UniqueTokenFilter(name: String, onlyOnSamePosition: Option[Boolean] = None) extends TokenFilterDefinition {

  val filterType = "unique"

  override def build(source: XContentBuilder): Unit =
    onlyOnSamePosition.foreach(source.field("only_on_same_position", _))

  def onlyOnSamePosition(onlyOnSamePosition: Boolean): UniqueTokenFilter =
    copy(onlyOnSamePosition = onlyOnSamePosition.some)
}

case class KeywordMarkerTokenFilter(name: String,
                                    keywords: Seq[String] = Nil,
                                    keywordsPath: Option[String] = None,
                                    keywordsPattern: Option[String] = None,
                                    ignoreCase: Option[Boolean] = None)
    extends TokenFilterDefinition {

  val filterType = "keyword_marker"

  override def build(source: XContentBuilder): Unit = {
    if (keywords.nonEmpty)
      source.array("keywords", keywords.toArray)

    keywordsPath.foreach(source.field("keywords_path", _))
    keywordsPattern.foreach(source.field("keywords_pattern", _))
    ignoreCase.foreach(source.field("ignore_case", _))
  }

  def keywords(keywords: Seq[String]): KeywordMarkerTokenFilter        = copy(keywords = keywords)
  def keywords(first: String, rest: String*): KeywordMarkerTokenFilter = copy(keywords = first +: rest)
  def keywordsPath(path: String): KeywordMarkerTokenFilter             = copy(keywordsPath = path.some)
  def keywordsPattern(pattern: String): KeywordMarkerTokenFilter       = copy(keywordsPattern = pattern.some)
  def ignoreCase(ignoreCase: Boolean): KeywordMarkerTokenFilter        = copy(ignoreCase = ignoreCase.some)
}

case class ElisionTokenFilter(name: String, articles: Seq[String] = Nil) extends TokenFilterDefinition {

  val filterType = "elision"

  override def build(source: XContentBuilder): Unit =
    source.array("articles", articles.toArray)

  def articles(articles: Seq[String]): ElisionTokenFilter        = copy(articles = articles)
  def articles(first: String, rest: String*): ElisionTokenFilter = copy(articles = first +: rest)
}

case class LimitTokenCountTokenFilter(name: String,
                                      maxTokenCount: Option[Int] = None,
                                      consumeAllTokens: Option[Boolean] = None)
    extends TokenFilterDefinition {

  val filterType = "limit"

  override def build(source: XContentBuilder): Unit = {
    maxTokenCount.foreach(source.field("max_token_count", _))
    consumeAllTokens.foreach(source.field("consume_all_tokens", _))
  }

  def maxTokenCount(maxTokenCount: Int): LimitTokenCountTokenFilter = copy(maxTokenCount = maxTokenCount.some)
  def consumeAllTokens(consumeAllTokens: Boolean): LimitTokenCountTokenFilter =
    copy(consumeAllTokens = consumeAllTokens.some)
}

case class StopTokenFilter(name: String,
                           language: Option[String] = None,
                           stopwords: Iterable[String] = Nil,
                           stopwordsPath: Option[String] = None,
                           enablePositionIncrements: Option[Boolean] = None, // ignored now as of 1.4.0
                           removeTrailing: Option[Boolean] = None,
                           ignoreCase: Option[Boolean] = None)
    extends TokenFilterDefinition {

  val filterType = "stop"

  override def build(source: XContentBuilder): Unit = {
    if (stopwords.nonEmpty)
      source.array("stopwords", stopwords.toArray)
    language.foreach(source.field("stopwords", _))
    stopwordsPath.foreach(source.field("stopwords_path", _))
    enablePositionIncrements.foreach(source.field("enable_position_increments", _))
    ignoreCase.foreach(source.field("ignore_case", _))
    removeTrailing.foreach(source.field("remove_trailing", _))
  }

  def ignoreCase(boolean: Boolean): StopTokenFilter                = copy(ignoreCase = boolean.some)
  def removeTrailing(boolean: Boolean): StopTokenFilter            = copy(removeTrailing = boolean.some)
  def enablePositionIncrements(boolean: Boolean): StopTokenFilter  = copy(enablePositionIncrements = boolean.some)
  def language(language: String): StopTokenFilter                  = copy(language = language.some)
  def stopwords(stopwords: Iterable[String]): StopTokenFilter      = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): StopTokenFilter = copy(stopwords = stopwords +: rest)
  def stopwordsPath(path: String): StopTokenFilter                 = copy(stopwordsPath = path.some)
}

object NamedStopTokenFilter {
  val Arabic     = "_arabic_"
  val Armenian   = "_armenian_"
  val Basque     = "_basque_"
  val Brazilian  = "_brazilian_"
  val Bulgarian  = "_bulgarian_"
  val Catalan    = "_catalan_"
  val Czech      = "_czech_"
  val Danish     = "_danish_"
  val Dutch      = "_dutch_"
  val English    = "_english_"
  val Finnish    = "_finnish_"
  val French     = "_french_"
  val Galician   = "_galician_"
  val German     = "_german_"
  val Greek      = "_greek_"
  val Hindi      = "_hindi_"
  val Hungarian  = "_hungarian_"
  val Indonesian = "_indonesian_"
  val Italian    = "_italian_"
  val Norwegian  = "_norwegian_"
  val Persian    = "_persian_"
  val Portuguese = "_portuguese_"
  val Romanian   = "_romanian_"
  val Russian    = "_russian_"
  val Spanish    = "_spanish_"
  val Swedish    = "_swedish_"
  val Turkish    = "_turkish_"
}

case class PatternCaptureTokenFilter(name: String, patterns: Seq[String] = Nil, preserveOriginal: Boolean = true)
    extends TokenFilterDefinition {

  val filterType = "pattern_capture"

  override def build(source: XContentBuilder): Unit = {
    if (patterns.nonEmpty)
      source.array("patterns", patterns.toArray)
    source.field("preserve_original", preserveOriginal)
  }

  def patterns(patterns: Seq[String]): PatternCaptureTokenFilter             = copy(patterns = patterns)
  def patterns(first: String, rest: String*): PatternCaptureTokenFilter      = copy(patterns = first +: rest)
  def preserveOriginal(preserveOriginal: Boolean): PatternCaptureTokenFilter = copy(preserveOriginal = preserveOriginal)
}

case class PatternReplaceTokenFilter(name: String, pattern: String, replacement: String) extends TokenFilterDefinition {

  val filterType = "pattern_replace"

  override def build(source: XContentBuilder): Unit = {
    source.field("pattern", pattern)
    source.field("replacement", replacement)
  }

  def pattern(p: String): PatternReplaceTokenFilter     = copy(pattern = p)
  def replacement(r: String): PatternReplaceTokenFilter = copy(replacement = r)
}

case class CommonGramsTokenFilter(name: String,
                                  commonWords: Iterable[String] = Nil,
                                  commonWordsPath: Option[String] = None,
                                  ignoreCase: Option[Boolean] = None,
                                  queryMode: Option[Boolean] = None)
    extends TokenFilterDefinition {

  val filterType = "common_grams"

  override def build(source: XContentBuilder): Unit = {
    if (commonWords.nonEmpty)
      source.array("common_words", commonWords.toArray)

    commonWordsPath.foreach(source.field("common_words_path", _))
    ignoreCase.foreach(source.field("ignore_case", _))
    queryMode.foreach(source.field("query_mode", _))
  }

  def commonWords(words: Iterable[String]): CommonGramsTokenFilter      = copy(commonWords = words)
  def commonWords(first: String, rest: String*): CommonGramsTokenFilter = copy(commonWords = first +: rest)
  def ignoreCase(ignoreCase: Boolean): CommonGramsTokenFilter           = copy(ignoreCase = ignoreCase.some)
  def queryMode(queryMode: Boolean): CommonGramsTokenFilter             = copy(queryMode = queryMode.some)
  def commonWordsPath(path: String): CommonGramsTokenFilter             = copy(commonWordsPath = path.some)
}

case class EdgeNGramTokenFilter(name: String,
                                minGram: Option[Int] = None,
                                maxGram: Option[Int] = None,
                                side: Option[String] = None)
    extends TokenFilterDefinition {

  val filterType = "edgeNGram"

  override def build(source: XContentBuilder): Unit = {
    minGram.foreach(source.field("min_gram", _))
    maxGram.foreach(source.field("max_gram", _))
    side.foreach(source.field("side", _))
  }

  def minMaxGrams(min: Int, max: Int): EdgeNGramTokenFilter = copy(minGram = min.some, maxGram = max.some)
  def minGram(min: Int): EdgeNGramTokenFilter               = copy(minGram = min.some)
  def maxGram(max: Int): EdgeNGramTokenFilter               = copy(maxGram = max.some)
  def side(side: String): EdgeNGramTokenFilter              = copy(side = side.some)
}

case class NGramTokenFilter(name: String, minGram: Option[Int] = None, maxGram: Option[Int] = None)
    extends TokenFilterDefinition {

  val filterType = "nGram"

  override def build(source: XContentBuilder): Unit = {
    minGram.foreach(source.field("min_gram", _))
    maxGram.foreach(source.field("max_gram", _))
  }

  def minMaxGrams(min: Int, max: Int): NGramTokenFilter = copy(minGram = min.some, maxGram = max.some)
  def minGram(min: Int): NGramTokenFilter               = copy(minGram = min.some)
  def maxGram(max: Int): NGramTokenFilter               = copy(maxGram = max.some)
}

case class SnowballTokenFilter(name: String, language: String) extends TokenFilterDefinition {

  val filterType = "snowball"

  override def build(source: XContentBuilder): Unit =
    source.field("language", language)

  def lang(l: String): SnowballTokenFilter = copy(language = l)
}

case class StemmerTokenFilter(name: String, lang: String) extends TokenFilterDefinition {

  val filterType = "stemmer"

  override def build(source: XContentBuilder): Unit =
    source.field("name", lang)

  def lang(l: String): StemmerTokenFilter = copy(lang = l)
}

case class StemmerOverrideTokenFilter(name: String, rules: Seq[String] = Nil, rulesPath: Option[String] = None)
    extends TokenFilterDefinition {

  val filterType = "stemmer_override"

  override def build(source: XContentBuilder): Unit = {
    if (rules.nonEmpty)
      source.array("rules", rules.toArray)

    rulesPath.foreach(source.field("rules_path", _))
  }

  def rules(rules: Array[String]): StemmerOverrideTokenFilter = copy(rules = rules)
  def rulesPath(path: String): StemmerOverrideTokenFilter     = copy(rulesPath = path.some)
}

case class WordDelimiterTokenFilter(name: String,
                                    generateWordParts: Option[Boolean] = None,
                                    generateNumberParts: Option[Boolean] = None,
                                    catenateWords: Option[Boolean] = None,
                                    catenateNumbers: Option[Boolean] = None,
                                    catenateAll: Option[Boolean] = None,
                                    splitOnCaseChange: Option[Boolean] = None,
                                    preserveOriginal: Option[Boolean] = None,
                                    splitOnNumerics: Option[Boolean] = None,
                                    stemEnglishPossesive: Option[Boolean] = None)
    extends TokenFilterDefinition {

  val filterType = "word_delimiter"

  override def build(source: XContentBuilder): Unit = {
    generateWordParts.foreach(source.field("generate_word_parts", _))
    generateNumberParts.foreach(source.field("generate_number_parts", _))
    catenateWords.foreach(source.field("catenate_words", _))
    catenateNumbers.foreach(source.field("catenate_numbers", _))
    catenateAll.foreach(source.field("catenate_all", _))
    splitOnCaseChange.foreach(source.field("split_on_case_change", _))
    preserveOriginal.foreach(source.field("preserve_original", _))
    splitOnNumerics.foreach(source.field("split_on_numerics", _))
    stemEnglishPossesive.foreach(source.field("stem_english_possessive", _))
  }

  def generateWordParts(bool: Boolean): WordDelimiterTokenFilter    = copy(generateWordParts = bool.some)
  def generateNumberParts(bool: Boolean): WordDelimiterTokenFilter  = copy(generateNumberParts = bool.some)
  def catenateWords(bool: Boolean): WordDelimiterTokenFilter        = copy(catenateWords = bool.some)
  def catenateNumbers(bool: Boolean): WordDelimiterTokenFilter      = copy(catenateNumbers = bool.some)
  def catenateAll(bool: Boolean): WordDelimiterTokenFilter          = copy(catenateAll = bool.some)
  def splitOnCaseChange(bool: Boolean): WordDelimiterTokenFilter    = copy(splitOnCaseChange = bool.some)
  def preserveOriginal(bool: Boolean): WordDelimiterTokenFilter     = copy(preserveOriginal = bool.some)
  def splitOnNumerics(bool: Boolean): WordDelimiterTokenFilter      = copy(splitOnNumerics = bool.some)
  def stemEnglishPossesive(bool: Boolean): WordDelimiterTokenFilter = copy(stemEnglishPossesive = bool.some)
}

case class ShingleTokenFilter(name: String,
                              maxShingleSize: Option[Int] = None,
                              minShingleSize: Option[Int] = None,
                              outputUnigrams: Option[Boolean] = None,
                              outputUnigramsIfNoShingles: Option[Boolean] = None,
                              tokenSeparator: Option[String] = None,
                              fillerToken: Option[String] = None)
    extends TokenFilterDefinition {

  val filterType = "shingle"

  override def build(source: XContentBuilder): Unit = {
    maxShingleSize.foreach(source.field("max_shingle_size", _))
    minShingleSize.foreach(source.field("min_shingle_size", _))
    outputUnigrams.foreach(source.field("output_unigrams", _))
    outputUnigramsIfNoShingles.foreach(source.field("output_unigrams_if_no_shingles", _))
    tokenSeparator.foreach(source.field("token_separator", _))
    fillerToken.foreach(source.field("filler_token", _))
  }

  def maxShingleSize(max: Int): ShingleTokenFilter               = copy(maxShingleSize = max.some)
  def minShingleSize(min: Int): ShingleTokenFilter               = copy(minShingleSize = min.some)
  def outputUnigrams(b: Boolean): ShingleTokenFilter             = copy(outputUnigrams = b.some)
  def outputUnigramsIfNoShingles(b: Boolean): ShingleTokenFilter = copy(outputUnigramsIfNoShingles = b.some)
  def tokenSeparator(sep: String): ShingleTokenFilter            = copy(tokenSeparator = sep.some)
  def fillerToken(filler: String): ShingleTokenFilter            = copy(fillerToken = filler.some)
}

sealed trait CompoundWordTokenFilterType {
  def name: String
}
case object HyphenationDecompounder extends CompoundWordTokenFilterType {
  override def name = "hyphenation_decompounder"
}

case object DictionaryDecompounder extends CompoundWordTokenFilterType {
  override def name = "dictionary_decompounder"
}

case class CompoundWordTokenFilter(name: String,
                                   `type`: CompoundWordTokenFilterType,
                                   wordList: Iterable[String] = Nil,
                                   wordListPath: Option[String] = None,
                                   hyphenationPatternsPath: Option[String] = None,
                                   minWordSize: Option[Int] = None,
                                   minSubwordSize: Option[Int] = None,
                                   maxSubwordSize: Option[Int] = None,
                                   onlyLongestMatch: Option[Boolean] = None)
    extends TokenFilterDefinition {

  val filterType: String = `type`.name

  override def build(source: XContentBuilder): Unit = {
    if (wordList.nonEmpty)
      source.array("word_list", wordList.toArray)
    wordListPath.foreach(source.field("word_list_path", _))
    hyphenationPatternsPath.foreach(source.field("hyphenation_patterns_path", _))
    minWordSize.foreach(source.field("min_word_size", _))
    minSubwordSize.foreach(source.field("min_subword_size", _))
    maxSubwordSize.foreach(source.field("max_subword_size", _))
    onlyLongestMatch.foreach(source.field("only_longest_match", _))
  }

  def wordList(wordList: Iterable[String]): CompoundWordTokenFilter =
    copy(wordList = wordList)
  def wordList(word: String, rest: String*): CompoundWordTokenFilter =
    copy(wordList = word +: rest)
  def wordListPath(wordListPath: String): CompoundWordTokenFilter =
    copy(wordListPath = wordListPath.some)
  def hyphenationPatternsPath(hyphenationPatternsPath: String): CompoundWordTokenFilter =
    copy(hyphenationPatternsPath = hyphenationPatternsPath.some)
  def minWordSize(minWordSize: Int): CompoundWordTokenFilter =
    copy(minWordSize = minWordSize.some)
  def minSubwordSize(minSubwordSize: Int): CompoundWordTokenFilter =
    copy(minSubwordSize = minSubwordSize.some)
  def maxSubwordSize(maxSubwordSize: Int): CompoundWordTokenFilter =
    copy(maxSubwordSize = maxSubwordSize.some)
  def onlyLongestMatch(onlyLongestMatch: Boolean): CompoundWordTokenFilter =
    copy(onlyLongestMatch = onlyLongestMatch.some)
}

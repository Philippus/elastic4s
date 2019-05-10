package com.sksamuel.elastic4s.requests.analysis

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}
import com.sksamuel.exts.OptionImplicits._

trait TokenFilter {
  def name: String
  def build: XContentBuilder
}

case class SynonymTokenFilter(override val name: String,
                              path: Option[String] = None,
                              synonyms: Set[String] = Set.empty,
                              ignoreCase: Option[Boolean] = None,
                              format: Option[String] = None,
                              expand: Option[Boolean] = None,
                              tokenizer: Option[String] = None) extends TokenFilter {
  require(path.isDefined || synonyms.nonEmpty, "synonym requires either `synonyms` or `synonyms_path` to be configured")

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "synonym")
    if (synonyms.isEmpty) path.foreach(b.field("synonyms_path", _))
    if (synonyms.nonEmpty) b.array("synonyms", synonyms.toArray)
    format.foreach(b.field("format", _))
    ignoreCase.foreach(b.field("ignore_case", _))
    expand.foreach(b.field("expand", _))
    tokenizer.foreach(b.field("tokenizer", _))
    b
  }

  def path(path: String): SynonymTokenFilter = copy(path = Some(path))
  def synonyms(synonyms: Iterable[String]): SynonymTokenFilter = copy(synonyms = synonyms.toSet)
  def tokenizer(tokenizer: String): SynonymTokenFilter = copy(tokenizer = tokenizer.some)
  def format(format: String): SynonymTokenFilter = copy(format = format.some)
  def ignoreCase(ignoreCase: Boolean): SynonymTokenFilter = copy(ignoreCase = ignoreCase.some)
  def expand(expand: Boolean): SynonymTokenFilter = copy(expand = expand.some)
}

case class SynonymGraphTokenFilter(override val name: String,
                                   path: Option[String] = None,
                                   synonyms: Set[String] = Set.empty,
                                   ignoreCase: Option[Boolean] = None,
                                   format: Option[String] = None,
                                   expand: Option[Boolean] = None,
                                   tokenizer: Option[String] = None) extends TokenFilter {

  require(path.isDefined || synonyms.nonEmpty, "synonym_graph requires either `synonyms` or `synonyms_path` to be configured")

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "synonym_graph")
    if (synonyms.isEmpty) path.foreach(b.field("synonyms_path", _))
    if (synonyms.nonEmpty) b.array("synonyms", synonyms.toArray)
    format.foreach(b.field("format", _))
    ignoreCase.foreach(b.field("ignore_case", _))
    expand.foreach(b.field("expand", _))
    tokenizer.foreach(b.field("tokenizer", _))
    b
  }

  def path(path: String): SynonymGraphTokenFilter = copy(path = Some(path))
  def synonyms(synonyms: Iterable[String]): SynonymGraphTokenFilter = copy(synonyms = synonyms.toSet)
  def tokenizer(tokenizer: String): SynonymGraphTokenFilter = copy(tokenizer = tokenizer.some)
  def format(format: String): SynonymGraphTokenFilter = copy(format = Some(format))
  def ignoreCase(ignoreCase: Boolean): SynonymGraphTokenFilter = copy(ignoreCase = Some(ignoreCase))
  def expand(expand: Boolean): SynonymGraphTokenFilter = copy(expand = Some(expand))
}

case class TruncateTokenFilter(override val name: String,
                               length: Int) extends TokenFilter {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "truncate")
    b.field("length", length)
    b
  }
}

case class LengthTokenFilter(override val name: String,
                             min: Option[Int] = None,
                             max: Option[Int] = None) extends TokenFilter {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "length")
    min.foreach(b.field("min", _))
    max.foreach(b.field("max", _))
    b
  }

  def min(min: Int): LengthTokenFilter = copy(min = min.some)
  def max(max: Int): LengthTokenFilter = copy(max = max.some)
}

case class UniqueTokenFilter(override val name: String,
                             onlyOnSamePosition: Boolean) extends TokenFilter {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "unique")
    b.field("only_on_same_position", onlyOnSamePosition)
    b
  }
}

case class KeywordMarkerTokenFilter(override val name: String,
                                    keywords: Seq[String] = Nil,
                                    keywordsPath: Option[String] = None,
                                    keywordsPattern: Option[String] = None,
                                    ignoreCase: Option[Boolean] = None) extends TokenFilter {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "keyword_marker")

    if (keywords.nonEmpty)
      b.array("keywords", keywords.toArray)

    keywordsPath.foreach(b.field("keywords_path", _))
    keywordsPattern.foreach(b.field("keywords_pattern", _))
    ignoreCase.foreach(b.field("ignore_case", _))
    b
  }

  def keywords(keywords: Seq[String]): KeywordMarkerTokenFilter = copy(keywords = keywords)
  def keywords(first: String, rest: String*): KeywordMarkerTokenFilter = copy(keywords = first +: rest)
  def keywordsPath(path: String): KeywordMarkerTokenFilter = copy(keywordsPath = path.some)
  def keywordsPattern(pattern: String): KeywordMarkerTokenFilter = copy(keywordsPattern = pattern.some)
  def ignoreCase(ignoreCase: Boolean): KeywordMarkerTokenFilter = copy(ignoreCase = ignoreCase.some)
}

case class ElisionTokenFilter(override val name: String, articles: Seq[String] = Nil) extends TokenFilter {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "elision")
    b.array("articles", articles.toArray)
    b
  }

  def articles(articles: Seq[String]): ElisionTokenFilter = copy(articles = articles)
  def articles(first: String, rest: String*): ElisionTokenFilter = copy(articles = first +: rest)
}

case class LimitTokenCountTokenFilter(override val name: String,
                                      maxTokenCount: Option[Int] = None,
                                      consumeAllTokens: Option[Boolean] = None) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "limit")
    maxTokenCount.foreach(b.field("max_token_count", _))
    consumeAllTokens.foreach(b.field("consume_all_tokens", _))
    b
  }

  def maxTokenCount(maxTokenCount: Int): LimitTokenCountTokenFilter = copy(maxTokenCount = maxTokenCount.some)
  def consumeAllTokens(consumeAllTokens: Boolean): LimitTokenCountTokenFilter =
    copy(consumeAllTokens = consumeAllTokens.some)
}

case class StopTokenFilter(override val name: String,
                           language: Option[String] = None,
                           stopwords: Iterable[String] = Nil,
                           stopwordsPath: Option[String] = None,
                           enablePositionIncrements: Option[Boolean] = None, // ignored now as of 1.4.0
                           removeTrailing: Option[Boolean] = None,
                           ignoreCase: Option[Boolean] = None) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "stop")
    if (stopwords.nonEmpty)
      b.array("stopwords", stopwords.toArray)
    language.foreach(b.field("stopwords", _))
    stopwordsPath.foreach(b.field("stopwords_path", _))
    enablePositionIncrements.foreach(b.field("enable_position_increments", _))
    ignoreCase.foreach(b.field("ignore_case", _))
    removeTrailing.foreach(b.field("remove_trailing", _))
    b
  }

  def ignoreCase(boolean: Boolean): StopTokenFilter = copy(ignoreCase = boolean.some)
  def removeTrailing(boolean: Boolean): StopTokenFilter = copy(removeTrailing = boolean.some)
  def enablePositionIncrements(boolean: Boolean): StopTokenFilter = copy(enablePositionIncrements = boolean.some)
  def language(language: String): StopTokenFilter = copy(language = language.some)
  def stopwords(stopwords: Iterable[String]): StopTokenFilter = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): StopTokenFilter = copy(stopwords = stopwords +: rest)
  def stopwordsPath(path: String): StopTokenFilter = copy(stopwordsPath = path.some)
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

case class PatternCaptureTokenFilter(override val name: String,
                                     patterns: Seq[String] = Nil,
                                     preserveOriginal: Boolean = true) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "pattern_capture")
    if (patterns.nonEmpty)
      b.array("patterns", patterns.toArray)
    b.field("preserve_original", preserveOriginal)
    b
  }

  def patterns(patterns: Seq[String]): PatternCaptureTokenFilter = copy(patterns = patterns)
  def patterns(first: String, rest: String*): PatternCaptureTokenFilter = copy(patterns = first +: rest)
  def preserveOriginal(preserveOriginal: Boolean): PatternCaptureTokenFilter = copy(preserveOriginal = preserveOriginal)
}

case class HunspellTokenFilter(override val name: String,
                               locale: String,
                               dedup: Option[Boolean] = None,
                               longestOnly: Option[Boolean] = None,
                               dictionary: Option[String] = None) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "hunspell")
    b.field("locale", locale)
    dictionary.foreach(b.field("dictionary", _))
    dedup.foreach(b.field("dedup", _))
    longestOnly.foreach(b.field("longest_only", _))
    b
  }

  def locale(locale: String): HunspellTokenFilter = copy(locale = locale)
  def longestOnly(longestOnly: Boolean): HunspellTokenFilter = copy(longestOnly = longestOnly.some)
  def dedup(dedup: Boolean): HunspellTokenFilter = copy(dedup = dedup.some)
  def dictionary(dictionary: String): HunspellTokenFilter = copy(dictionary = dictionary.some)
}

case class PatternReplaceTokenFilter(override val name: String,
                                     pattern: String,
                                     replacement: String) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "pattern_replace")
    b.field("pattern", pattern)
    b.field("replacement", replacement)
    b
  }

  def pattern(p: String): PatternReplaceTokenFilter = copy(pattern = p)
  def replacement(r: String): PatternReplaceTokenFilter = copy(replacement = r)
}

case class CommonGramsTokenFilter(override val name: String,
                                  commonWords: Iterable[String] = Nil,
                                  commonWordsPath: Option[String] = None,
                                  ignoreCase: Option[Boolean] = None,
                                  queryMode: Option[Boolean] = None) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "common_grams")
    if (commonWords.nonEmpty)
      b.array("common_words", commonWords.toArray)
    commonWordsPath.foreach(b.field("common_words_path", _))
    ignoreCase.foreach(b.field("ignore_case", _))
    queryMode.foreach(b.field("query_mode", _))
    b
  }

  def commonWords(words: Iterable[String]): CommonGramsTokenFilter = copy(commonWords = words)
  def commonWords(first: String, rest: String*): CommonGramsTokenFilter = copy(commonWords = first +: rest)
  def ignoreCase(ignoreCase: Boolean): CommonGramsTokenFilter = copy(ignoreCase = ignoreCase.some)
  def queryMode(queryMode: Boolean): CommonGramsTokenFilter = copy(queryMode = queryMode.some)
  def commonWordsPath(path: String): CommonGramsTokenFilter = copy(commonWordsPath = path.some)
}

case class EdgeNGramTokenFilter(override val name: String,
                                minGram: Option[Int] = None,
                                maxGram: Option[Int] = None,
                                side: Option[String] = None) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "edgeNGram")
    minGram.foreach(b.field("min_gram", _))
    maxGram.foreach(b.field("max_gram", _))
    side.foreach(b.field("side", _))
    b
  }

  def minMaxGrams(min: Int, max: Int): EdgeNGramTokenFilter = copy(minGram = min.some, maxGram = max.some)
  def minGram(min: Int): EdgeNGramTokenFilter = copy(minGram = min.some)
  def maxGram(max: Int): EdgeNGramTokenFilter = copy(maxGram = max.some)
  def side(side: String): EdgeNGramTokenFilter = copy(side = side.some)
}

case class NGramTokenFilter(override val name: String,
                            minGram: Option[Int] = None,
                            maxGram: Option[Int] = None) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "nGram")
    minGram.foreach(b.field("min_gram", _))
    maxGram.foreach(b.field("max_gram", _))
    b
  }

  def minMaxGrams(min: Int, max: Int): NGramTokenFilter = copy(minGram = min.some, maxGram = max.some)
  def minGram(min: Int): NGramTokenFilter = copy(minGram = min.some)
  def maxGram(max: Int): NGramTokenFilter = copy(maxGram = max.some)
}

case class SnowballTokenFilter(override val name: String,
                               language: String) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "snowball")
    b.field("language", language)
    b
  }

  def lang(l: String): SnowballTokenFilter = copy(language = l)
}

case class StemmerTokenFilter(override val name: String,
                              lang: String) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "stemmer")
    b.field("name", lang)
    b
  }

  def lang(l: String): StemmerTokenFilter = copy(lang = l)
}

case class StemmerOverrideTokenFilter(override val name: String,
                                      rules: Seq[String] = Nil,
                                      rulesPath: Option[String] = None) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "stemmer_override")
    if (rules.nonEmpty)
      b.array("rules", rules.toArray)
    rulesPath.foreach(b.field("rules_path", _))
    b
  }

  def rules(rules: Array[String]): StemmerOverrideTokenFilter = copy(rules = rules)
  def rulesPath(path: String): StemmerOverrideTokenFilter = copy(rulesPath = path.some)
}

case class WordDelimiterTokenFilter(override val name: String,
                                    generateWordParts: Option[Boolean] = None,
                                    generateNumberParts: Option[Boolean] = None,
                                    catenateWords: Option[Boolean] = None,
                                    catenateNumbers: Option[Boolean] = None,
                                    catenateAll: Option[Boolean] = None,
                                    splitOnCaseChange: Option[Boolean] = None,
                                    preserveOriginal: Option[Boolean] = None,
                                    splitOnNumerics: Option[Boolean] = None,
                                    stemEnglishPossesive: Option[Boolean] = None) extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "word_delimiter")
    generateWordParts.foreach(b.field("generate_word_parts", _))
    generateNumberParts.foreach(b.field("generate_number_parts", _))
    catenateWords.foreach(b.field("catenate_words", _))
    catenateNumbers.foreach(b.field("catenate_numbers", _))
    catenateAll.foreach(b.field("catenate_all", _))
    splitOnCaseChange.foreach(b.field("split_on_case_change", _))
    preserveOriginal.foreach(b.field("preserve_original", _))
    splitOnNumerics.foreach(b.field("split_on_numerics", _))
    stemEnglishPossesive.foreach(b.field("stem_english_possessive", _))
    b
  }

  def generateWordParts(bool: Boolean): WordDelimiterTokenFilter = copy(generateWordParts = bool.some)
  def generateNumberParts(bool: Boolean): WordDelimiterTokenFilter = copy(generateNumberParts = bool.some)
  def catenateWords(bool: Boolean): WordDelimiterTokenFilter = copy(catenateWords = bool.some)
  def catenateNumbers(bool: Boolean): WordDelimiterTokenFilter = copy(catenateNumbers = bool.some)
  def catenateAll(bool: Boolean): WordDelimiterTokenFilter = copy(catenateAll = bool.some)
  def splitOnCaseChange(bool: Boolean): WordDelimiterTokenFilter = copy(splitOnCaseChange = bool.some)
  def preserveOriginal(bool: Boolean): WordDelimiterTokenFilter = copy(preserveOriginal = bool.some)
  def splitOnNumerics(bool: Boolean): WordDelimiterTokenFilter = copy(splitOnNumerics = bool.some)
  def stemEnglishPossesive(bool: Boolean): WordDelimiterTokenFilter = copy(stemEnglishPossesive = bool.some)
}

case class ShingleTokenFilter(override val name: String,
                              maxShingleSize: Option[Int] = None,
                              minShingleSize: Option[Int] = None,
                              outputUnigrams: Option[Boolean] = None,
                              outputUnigramsIfNoShingles: Option[Boolean] = None,
                              tokenSeparator: Option[String] = None,
                              fillerToken: Option[String] = None)
  extends TokenFilter {


  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "shingle")
    maxShingleSize.foreach(b.field("max_shingle_size", _))
    minShingleSize.foreach(b.field("min_shingle_size", _))
    outputUnigrams.foreach(b.field("output_unigrams", _))
    outputUnigramsIfNoShingles.foreach(b.field("output_unigrams_if_no_shingles", _))
    tokenSeparator.foreach(b.field("token_separator", _))
    fillerToken.foreach(b.field("filler_token", _))
    b
  }

  def maxShingleSize(max: Int): ShingleTokenFilter = copy(maxShingleSize = max.some)
  def minShingleSize(min: Int): ShingleTokenFilter = copy(minShingleSize = min.some)
  def outputUnigrams(b: Boolean): ShingleTokenFilter = copy(outputUnigrams = b.some)
  def outputUnigramsIfNoShingles(b: Boolean): ShingleTokenFilter = copy(outputUnigramsIfNoShingles = b.some)
  def tokenSeparator(sep: String): ShingleTokenFilter = copy(tokenSeparator = sep.some)
  def fillerToken(filler: String): ShingleTokenFilter = copy(fillerToken = filler.some)
}

sealed trait CompoundWordTokenFilterType {
  def name: String
}

case class CompoundWordTokenFilter(override val name: String,
                                   `type`: CompoundWordTokenFilterType,
                                   wordList: Iterable[String] = Nil,
                                   wordListPath: Option[String] = None,
                                   hyphenationPatternsPath: Option[String] = None,
                                   minWordSize: Option[Int] = None,
                                   minSubwordSize: Option[Int] = None,
                                   maxSubwordSize: Option[Int] = None,
                                   onlyLongestMatch: Option[Boolean] = None) extends TokenFilter {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", `type`.name)
    if (wordList.nonEmpty)
      b.array("word_list", wordList.toArray)
    wordListPath.foreach(b.field("word_list_path", _))
    hyphenationPatternsPath.foreach(b.field("hyphenation_patterns_path", _))
    minWordSize.foreach(b.field("min_word_size", _))
    minSubwordSize.foreach(b.field("min_subword_size", _))
    maxSubwordSize.foreach(b.field("max_subword_size", _))
    onlyLongestMatch.foreach(b.field("only_longest_match", _))
    b
  }

  def wordList(wordList: Iterable[String]): CompoundWordTokenFilter = copy(wordList = wordList)
  def wordList(word: String, rest: String*): CompoundWordTokenFilter = copy(wordList = word +: rest)
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

package com.sksamuel.elastic4s.requests.analysis

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

trait Tokenizer {
  def name: String
  def build: XContentBuilder
}

case class UaxUrlEmailTokenizer(override val name: String,
                                maxTokenLength: Int = 255) extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "uax_url_email")
    b.field("max_token_length", maxTokenLength)
    b.endObject()
  }

  def maxTokenLength(maxTokenLength: Int): UaxUrlEmailTokenizer = copy(maxTokenLength = maxTokenLength)
}

case class CharGroupTokenizer(override val name: String,
                              tokenizeOnChars: List[String]) extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "char_group")
    b.array("tokenize_on_chars", tokenizeOnChars)
    b.endObject()
  }
}


case class StandardTokenizer(override val name: String,
                             maxTokenLength: Int = 255) extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "standard")
    b.field("max_token_length", maxTokenLength)
    b.endObject()
  }

  def maxTokenLength(maxTokenLength: Int): StandardTokenizer = copy(maxTokenLength = maxTokenLength)
}

case class PatternTokenizer(override val name: String,
                            pattern: String = "\\W+",
                            flags: String = "", group: Int = -1) extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "pattern")
    if (flags.nonEmpty)
      b.field("flags", flags)
    b.field("pattern", pattern)
    if (group > 0)
      b.field("group", group)
    b.endObject()
  }

  def pattern(pattern: String): PatternTokenizer = copy(pattern = pattern)
  def flags(flags: String): PatternTokenizer = copy(flags = flags)
  def group(group: Int): PatternTokenizer = copy(group = group)
}

case class KeywordTokenizer(override val name: String,
                            bufferSize: Int = 256) extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "keyword")
    b.field("bufferSize", bufferSize)
  }

  def bufferSize(bufferSize: Int): KeywordTokenizer = copy(bufferSize = bufferSize)
}

case class NGramTokenizer(override val name: String,
                          minGram: Int = 1,
                          maxGram: Int = 2,
                          tokenChars: Iterable[String] = Nil) extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "nGram")
    b.field("min_gram", minGram)
    b.field("max_gram", maxGram)
    if (tokenChars.nonEmpty)
      b.array("token_chars", tokenChars.toArray)
    b.endObject()
  }

  def minMaxGrams(min: Int, max: Int): NGramTokenizer = copy(minGram = min, maxGram = max)
  def tokenChars(tokenChars: Iterable[String]): NGramTokenizer = copy(tokenChars = tokenChars)
  def tokenChars(tokenChar: String, rest: String*): NGramTokenizer = copy(tokenChars = tokenChar +: rest)
}

case class EdgeNGramTokenizer(override val name: String,
                              minGram: Int = 1,
                              maxGram: Int = 2,
                              tokenChars: Iterable[String] = Nil) extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "edgeNGram")
    b.field("min_gram", minGram)
    b.field("max_gram", maxGram)
    if (tokenChars.nonEmpty)
      b.array("token_chars", tokenChars.toArray)
    b.endObject()
  }

  def minMaxGrams(min: Int, max: Int): EdgeNGramTokenizer = copy(minGram = min, maxGram = max)
  def tokenChars(tokenChars: Iterable[String]): EdgeNGramTokenizer = copy(tokenChars = tokenChars)
  def tokenChars(tokenChar: String, rest: String*): EdgeNGramTokenizer = copy(tokenChars = tokenChar +: rest)
}

case class PathHierarchyTokenizer(override val name: String,
                                  delimiter: Char = '/',
                                  replacement: Char = '/',
                                  bufferSize: Int = 1024,
                                  reverse: Boolean = false,
                                  skip: Int = 0) extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "path_hierarchy")
    b.field("delimiter", delimiter.toString)
    b.field("replacement", replacement.toString)
    if (bufferSize > 1024) b.field("buffer_size", bufferSize)
    if (reverse) b.field("reverse", reverse)
    if (skip > 0) b.field("skip", skip)
    b.endObject()
  }
}

case class WhitespaceTokenizer(override val name: String,
                               maxTokenLength: Char = '/') extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "whitespace")
    b.field("max_token_length", maxTokenLength)
    b.endObject()
  }
}

case class ClassicTokenizer(override val name: String,
                            maxTokenLength: Char = '/') extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "classic")
    b.field("max_token_length", maxTokenLength)
    b.endObject()
  }
}

case class ThaiTokenizer(name: String) extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "thai")
    b.endObject()
  }
}

case class ICUTokenizer(override val name: String,
                        ruleFiles: String) extends Tokenizer {

  override def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "icu_tokenizer")
    b.field("rule_files", ruleFiles)
    b.endObject()
  }
}


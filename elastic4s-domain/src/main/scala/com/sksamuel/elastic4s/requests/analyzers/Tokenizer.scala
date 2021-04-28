package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

@deprecated("use new analysis package", "7.0.1")
abstract class Tokenizer(val name: String) {

  def build(source: XContentBuilder): Unit = {}

  def json: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder
    build(builder)
    builder.endObject()
  }

  def customized: Boolean = false
}

@deprecated("use new analysis package", "7.0.1")
case object WhitespaceTokenizer  extends Tokenizer("whitespace")

@deprecated("use new analysis package", "7.0.1")
case object LetterTokenizer      extends Tokenizer("letter")

@deprecated("use new analysis package", "7.0.1")
case object LowercaseTokenizer   extends Tokenizer("lowercase")

@deprecated("use new analysis package", "7.0.1")
case object StandardTokenizer    extends Tokenizer("standard")

@deprecated("use new analysis package", "7.0.1")
case object PatternTokenizer     extends Tokenizer("pattern")

@deprecated("use new analysis package", "7.0.1")
case object KeywordTokenizer     extends Tokenizer("keyword")

@deprecated("use new analysis package", "7.0.1")
case object NGramTokenizer       extends Tokenizer("nGram")

@deprecated("use new analysis package", "7.0.1")
case object EdgeNGramTokenizer   extends Tokenizer("edgeNGram")

@deprecated("use new analysis package", "7.0.1")
case object UaxUrlEmailTokenizer extends Tokenizer("uax_url_email")

@deprecated("use new analysis package", "7.0.1")
abstract class CustomizedTokenizer(override val name: String) extends Tokenizer(name) {
  override def customized: Boolean = true
}

@deprecated("use new analysis package", "7.0.1")
case class PredefinedTokenizer(override val name: String) extends Tokenizer(name)

@deprecated("use new analysis package", "7.7.0")
case class UaxUrlEmailTokenizer(override val name: String, maxTokenLength: Int = 255)
    extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "uax_url_email")
    source.field("max_token_length", maxTokenLength)
  }

  def maxTokenLength(maxTokenLength: Int): UaxUrlEmailTokenizer = copy(maxTokenLength = maxTokenLength)
}

@deprecated("use new analysis package", "7.0.1")
case class StandardTokenizer(override val name: String, maxTokenLength: Int = 255) extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "standard")
    source.field("max_token_length", maxTokenLength)
  }

  def maxTokenLength(maxTokenLength: Int): StandardTokenizer = copy(maxTokenLength = maxTokenLength)
}

@deprecated("use new analysis package", "7.0.1")
case class PatternTokenizer(override val name: String, pattern: String = "\\W+", flags: String = "", group: Int = -1)
    extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "pattern")
    if (flags.nonEmpty)
      source.field("flags", flags)
    source.field("pattern", pattern)
    if (group > 0)
      source.field("group", group)
  }

  @deprecated("use new analysis package", "7.7.0")
  def pattern(pattern: String): PatternTokenizer = copy(pattern = pattern)

  @deprecated("use new analysis package", "7.7.0")
  def flags(flags: String): PatternTokenizer     = copy(flags = flags)

  @deprecated("use new analysis package", "7.7.0")
  def group(group: Int): PatternTokenizer        = copy(group = group)
}

@deprecated("use new analysis package", "7.0.1")
case class KeywordTokenizer(override val name: String, bufferSize: Int = 256) extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "keyword")
    source.field("bufferSize", bufferSize)
  }

  @deprecated("use new analysis package", "7.7.0")
  def bufferSize(bufferSize: Int): KeywordTokenizer = copy(bufferSize = bufferSize)
}

@deprecated("use new analysis package", "7.0.1")
case class NGramTokenizer(override val name: String,
                          minGram: Int = 1,
                          maxGram: Int = 2,
                          tokenChars: Iterable[String] = Nil)
    extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "nGram")
    source.field("min_gram", minGram)
    source.field("max_gram", maxGram)
    if (tokenChars.nonEmpty)
      source.array("token_chars", tokenChars.toArray)
  }

  def minMaxGrams(min: Int, max: Int): NGramTokenizer              = copy(minGram = min, maxGram = max)
  def tokenChars(tokenChars: Iterable[String]): NGramTokenizer     = copy(tokenChars = tokenChars)
  def tokenChars(tokenChar: String, rest: String*): NGramTokenizer = copy(tokenChars = tokenChar +: rest)
}

@deprecated("use new analysis package", "7.0.1")
case class EdgeNGramTokenizer(override val name: String,
                              minGram: Int = 1,
                              maxGram: Int = 2,
                              tokenChars: Iterable[String] = Nil)
    extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "edgeNGram")
    source.field("min_gram", minGram)
    source.field("max_gram", maxGram)
    if (tokenChars.nonEmpty)
      source.array("token_chars", tokenChars.toArray)
  }

  def minMaxGrams(min: Int, max: Int): EdgeNGramTokenizer              = copy(minGram = min, maxGram = max)
  def tokenChars(tokenChars: Iterable[String]): EdgeNGramTokenizer     = copy(tokenChars = tokenChars)
  def tokenChars(tokenChar: String, rest: String*): EdgeNGramTokenizer = copy(tokenChars = tokenChar +: rest)
}

@deprecated("use new analysis package", "7.0.1")
case class PathHierarchyTokenizer(override val name: String,
                                  delimiter: Char = '/',
                                  replacement: Char = '/',
                                  bufferSize: Int = 1024,
                                  reverse: Boolean = false,
                                  skip: Int = 0)
    extends CustomizedTokenizer(name) {
  override def build(source: XContentBuilder): Unit = {
    source.field("type", "path_hierarchy")
    source.field("delimiter", delimiter.toString)
    source.field("replacement", replacement.toString)
    if (bufferSize > 1024) source.field("buffer_size", bufferSize)
    if (reverse) source.field("reverse", reverse)
    if (skip > 0) source.field("skip", skip)
  }
}

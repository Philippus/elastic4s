package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.XContentBuilder

/** @author Stephen Samuel */
abstract class Tokenizer(val `type`: String) {
  def build(source: XContentBuilder): Unit = {}
}

case object WhitespaceTokenizer extends Tokenizer("whitespace")

case object LetterTokenizer extends Tokenizer("letter")

case object LowercaseTokenizer extends Tokenizer("lowercase")

case class UaxUrlEmailTokenizer(maxtokenLength: Int = 255) extends Tokenizer("uax")

case class StandardTokenizer(name: String,
                             maxTokenLength: Int = 255) extends Tokenizer("standard")

case object StandardTokenizer extends Tokenizer("standard")

case class PatternTokenizer(name: String,
                            pattern: String = "\\W+",
                            flags: String = "",
                            group: Int = -1) extends Tokenizer("pattern")

case object PatternTokenizer extends Tokenizer("pattern")

case class KeywordTokenizer(name: String,
                            bufferSize: Int = 256) extends Tokenizer("keyword")

case object KeywordTokenizer extends Tokenizer("pattern")

case class NGramTokenizer(name: String,
                          minGram: Int = 1,
                          maxGram: Int = 2,
                          tokenChers: Array[Char] = Array()) extends Tokenizer("ngram")

case object NGramTokenizer extends Tokenizer("pattern")

case class EdgeNGramTokenizer(name: String,
                              minGram: Int = 1,
                              maxGram: Int = 2,
                              tokenChers: Array[Char] = Array()) extends Tokenizer("edgengram")

case object EdgeNGramTokenizer extends Tokenizer("pattern")

case class PathHierarchyTokenizer(name: String,
                                  delimiter: Char = '/',
                                  replacement: Char = '/',
                                  bufferSize: Int = 1024,
                                  reverse: Boolean = false,
                                  skip: Int = 0) extends Tokenizer("path")

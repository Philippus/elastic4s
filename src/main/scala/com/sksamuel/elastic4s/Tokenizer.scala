package com.sksamuel.elastic4s

/** @author Stephen Samuel */
abstract class Tokenizer
object Tokenizer {
  case object WhitespaceTokenizer extends Tokenizer
  case object LetterTokenizer extends Tokenizer
  case object LowercaseTokenizer extends Tokenizer
  case class UaxUrlEmailTokenizer(maxtokenLength: Int = 255) extends Tokenizer
  case class StandardTokenizer(maxTokenLength: Int = 255) extends Tokenizer
  case class PatternTokenizer(pattern: String = "\\W+", flags: String = "", group: Int = -1) extends Tokenizer
  case class KeywordTokenizer(bufferSize: Int = 256) extends Tokenizer
  case class NGramTokenizer(minGram: Int = 1, maxGram: Int = 2, tokenChers: Array[Char] = Array()) extends Tokenizer
  case class EdgeNGramTokenizer(minGram: Int = 1, maxGram: Int = 2, tokenChers: Array[Char] = Array()) extends Tokenizer
  case class PathHierarchyTokenizer(delimiter: Char = '/',
                                    replacement: Char = '/',
                                    bufferSize: Int = 1024,
                                    reverse: Boolean = false,
                                    skip: Int = 0) extends Tokenizer
}

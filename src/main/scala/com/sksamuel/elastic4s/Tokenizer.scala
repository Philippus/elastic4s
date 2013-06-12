package com.sksamuel.elastic4s

/** @author Stephen Samuel */
sealed trait Tokenizer
object Tokenizer {
    case object KeywordTokenizer extends Tokenizer
    case object WhitespaceTokenizer extends Tokenizer
    case object StandardTokenizer extends Tokenizer
    case object LetterTokenizer extends Tokenizer
}

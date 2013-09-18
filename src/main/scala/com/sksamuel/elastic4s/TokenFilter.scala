package com.sksamuel.elastic4s

/** @author Stephen Samuel */
trait TokenFilter
object TokenFilter {
  case object StandardTokenFilter extends TokenFilter
  case object AsciiFoldingTokenFilter extends TokenFilter
  case object KStemTokenFilter extends TokenFilter
  case object PorterStemTokenFilter extends TokenFilter
  case object ReverseTokenFilter extends TokenFilter
  case class TruncateTokenFilter(length: Int = 10) extends TokenFilter
  case class UniqueTokenFilter(onlyOnSamePosition: Boolean) extends TokenFilter
  case object TrimTokenFilter extends TokenFilter
  case class KeywordMarkerTokenFilter(keywords: String = "", keywordsPath: String = "", ignoreCase: Boolean = false)
    extends TokenFilter
  case class ElisionTokenFilter(articles: Array[String]) extends TokenFilter
  case class LimitTokenFilter(maxTokenCount: Int = 1, consumeAllTokens: Boolean = false)
  case class StopTokenFilter(stopwords: Array[String] = Array(),
                             stopwordsPath: String = "",
                             enablePositionIncrements: Boolean = true,
                             ignoreCase: Boolean = false)
  case class PatternCaptureTokenFilter(patterns: Array[String], preserveOriginal: Boolean = true) extends TokenFilter
  case class PatternReplaceTokenFilter(pattern: String, replacement: String) extends TokenFilter
  case class CommongGramsTokenFilter(commonWords: Array[String],
                                     commonWordsPath: String = "",
                                     ignoreCase: Boolean = false,
                                     queryMode: Boolean = false)
  case class SnowballTokenFilter(language: String) extends TokenFilter
  case class StemmerOverrideTokenFilter(rules: Array[String], rulesPath: String = "") extends TokenFilter
}

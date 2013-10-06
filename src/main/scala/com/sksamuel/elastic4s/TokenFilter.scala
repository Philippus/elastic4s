package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.XContentBuilder

/** @author Stephen Samuel */
abstract class TokenFilter(name: String) {
  def build(source: XContentBuilder): Unit = {}
}

case class StandardTokenFilter(name: String) extends TokenFilter(name)

case class AsciiFoldingTokenFilter(name: String) extends TokenFilter(name)

case class KStemTokenFilter(name: String) extends TokenFilter(name)

case class PorterStemTokenFilter(name: String) extends TokenFilter(name)

case class ReverseTokenFilter(name: String) extends TokenFilter(name)

case class TruncateTokenFilter(name: String, length: Int = 10) extends TokenFilter(name)

case class LengthTokenFilter(name: String, min: Int = 0, max: Int = Integer.MAX_VALUE) extends TokenFilter(name)

case class UniqueTokenFilter(name: String, onlyOnSamePosition: Boolean = false) extends TokenFilter(name)

case class TrimTokenFilter(name: String) extends TokenFilter(name)

case class KeywordMarkerTokenFilter(name: String,
                                    keywords: String = "",
                                    keywordsPath: String = "",
                                    ignoreCase: Boolean = false) extends TokenFilter(name)

case class ElisionTokenFilter(name: String,
                              articles: Array[String]) extends TokenFilter(name)

case class LimitTokenFilter(name: String,
                            maxTokenCount: Int = 1,
                            consumeAllTokens: Boolean = false) extends TokenFilter(name)

case class StopTokenFilter(name: String,
                           stopwords: Array[String] = Array(),
                           stopwordsPath: String = "",
                           enablePositionIncrements: Boolean = true,
                           ignoreCase: Boolean = false) extends TokenFilter(name)

case class PatternCaptureTokenFilter(name: String,
                                     patterns: Array[String],
                                     preserveOriginal: Boolean = true) extends TokenFilter(name)

case class PatternReplaceTokenFilter(name: String,
                                     pattern: String,
                                     replacement: String) extends TokenFilter(name)

case class CommongGramsTokenFilter(name: String,
                                   commonWords: Array[String],
                                   commonWordsPath: String = "",
                                   ignoreCase: Boolean = false,
                                   queryMode: Boolean = false) extends TokenFilter(name)

case class SnowballTokenFilter(name: String, language: String) extends TokenFilter(name)

case class StemmerOverrideTokenFilter(name: String,
                                      rules: Array[String],
                                      rulesPath: String = "") extends TokenFilter(name)

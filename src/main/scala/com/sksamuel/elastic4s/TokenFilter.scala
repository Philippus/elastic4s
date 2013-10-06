package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.XContentBuilder

/** @author Stephen Samuel */
abstract class TokenFilter(val name: String) {
  def build(source: XContentBuilder): Unit = {}
}

case class StandardTokenFilter(override val name: String) extends TokenFilter(name)

case class AsciiFoldingTokenFilter(override val name: String) extends TokenFilter(name)

case class KStemTokenFilter(override val name: String) extends TokenFilter(name)

case class PorterStemTokenFilter(override val name: String) extends TokenFilter(name)

case class ReverseTokenFilter(override val name: String) extends TokenFilter(name)

case class TruncateTokenFilter(override val name: String, length: Int = 10) extends TokenFilter(name)

case class LengthTokenFilter(override val name: String, min: Int = 0, max: Int = Integer.MAX_VALUE)
  extends TokenFilter(name)

case class UniqueTokenFilter(override val name: String, onlyOnSamePosition: Boolean = false) extends TokenFilter(name)

case class TrimTokenFilter(override val name: String) extends TokenFilter(name)

case class KeywordMarkerTokenFilter(override val name: String,
                                    keywords: String = "",
                                    keywordsPath: String = "",
                                    ignoreCase: Boolean = false) extends TokenFilter(name)

case class ElisionTokenFilter(override val name: String,
                              articles: Array[String]) extends TokenFilter(name)

case class LimitTokenFilter(override val name: String,
                            maxTokenCount: Int = 1,
                            consumeAllTokens: Boolean = false) extends TokenFilter(name)

case class StopTokenFilter(override val name: String,
                           stopwords: Array[String] = Array(),
                           stopwordsPath: String = "",
                           enablePositionIncrements: Boolean = true,
                           ignoreCase: Boolean = false) extends TokenFilter(name)

case class PatternCaptureTokenFilter(override val name: String,
                                     patterns: Array[String],
                                     preserveOriginal: Boolean = true) extends TokenFilter(name)

case class PatternReplaceTokenFilter(override val name: String,
                                     pattern: String,
                                     replacement: String) extends TokenFilter(name)

case class CommongGramsTokenFilter(override val name: String,
                                   commonWords: Array[String],
                                   commonWordsPath: String = "",
                                   ignoreCase: Boolean = false,
                                   queryMode: Boolean = false) extends TokenFilter(name)

case class SnowballTokenFilter(override val name: String, language: String) extends TokenFilter(name)

case class StemmerOverrideTokenFilter(override val name: String,
                                      rules: Array[String],
                                      rulesPath: String = "") extends TokenFilter(name)

package com.sksamuel.elastic4s.requests.analysis

import com.sksamuel.elastic4s.XContentBuilder

case class Analyzer(name: String)

case class CustomAnalyzer(charFilters: List[CharFilter])

trait BuiltInAnalyzer {
  def name: String
}

case class StopAnalyzer(override val name: String,
                        stopwords: Iterable[String] = Nil) extends BuiltInAnalyzer {

  def build(source: XContentBuilder): Unit = {
    source.field("type", "stop")
    source.array("stopwords", stopwords.toArray)
  }

  def stopwords(stopwords: Iterable[String]): StopAnalyzer = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): StopAnalyzer = copy(stopwords = stopwords +: rest)
}

case class StandardAnalyzer(override val name: String,
                            stopwords: Iterable[String] = Nil,
                            maxTokenLength: Int = 255) extends BuiltInAnalyzer {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "standard")
    source.array("stopwords", stopwords.toArray)
    source.field("max_token_length", maxTokenLength)
  }

  def stopwords(stopwords: Iterable[String]): StandardAnalyzer = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): StandardAnalyzer = copy(stopwords = stopwords +: rest)
  def maxTokenLength(maxTokenLength: Int): StandardAnalyzer = copy(maxTokenLength = maxTokenLength)
}

case class FingerprintAnalyzer(override val name: String,
                               separator: Option[String] = None,
                               stopwords: Iterable[String] = Nil,
                               maxOutputSize: Int = 255) extends BuiltInAnalyzer {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "standard")
    separator.foreach(source.field("separator", _))
    source.array("stopwords", stopwords.toArray)
    source.field("max_output_size", maxOutputSize)
  }

  def stopwords(stopwords: Iterable[String]): FingerprintAnalyzer = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): FingerprintAnalyzer = copy(stopwords = stopwords +: rest)
  def maxOutputSize(maxOutputSize: Int): FingerprintAnalyzer = copy(maxOutputSize = maxOutputSize)
}

case class PatternAnalyzer(override val name: String,
                           regex: String,
                           lowercase: Boolean = true) extends BuiltInAnalyzer {
  def build(source: XContentBuilder): Unit = {
    source.field("type", "pattern")
    source.field("lowercase", lowercase)
    source.field("pattern", regex)
  }

  def lowercase(lowercase: Boolean): PatternAnalyzer = copy(lowercase = lowercase)
}

package com.sksamuel.elastic4s.requests.analysis

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

trait Analyzer {
  def name: String
  def build: XContentBuilder
}

case class StopAnalyzer(override val name: String,
                        stopwords: Iterable[String] = Nil) extends Analyzer {

  def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "stop")
    b.array("stopwords", stopwords.toArray)
    b.endObject()
  }

  def stopwords(stopwords: Iterable[String]): StopAnalyzer = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): StopAnalyzer = copy(stopwords = stopwords +: rest)
}

case class StandardAnalyzer(override val name: String,
                            stopwords: Iterable[String] = Nil,
                            maxTokenLength: Int = 255) extends Analyzer {
  def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "standard")
    b.array("stopwords", stopwords.toArray)
    b.field("max_token_length", maxTokenLength)
    b.endObject()
  }

  def stopwords(stopwords: Iterable[String]): StandardAnalyzer = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): StandardAnalyzer = copy(stopwords = stopwords +: rest)
  def maxTokenLength(maxTokenLength: Int): StandardAnalyzer = copy(maxTokenLength = maxTokenLength)
}

case class FingerprintAnalyzer(override val name: String,
                               separator: Option[String] = None,
                               stopwords: Iterable[String] = Nil,
                               maxOutputSize: Int = 255) extends Analyzer {
  def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "standard")
    separator.foreach(b.field("separator", _))
    b.array("stopwords", stopwords.toArray)
    b.field("max_output_size", maxOutputSize)
    b.endObject()
  }

  def stopwords(stopwords: Iterable[String]): FingerprintAnalyzer = copy(stopwords = stopwords)
  def stopwords(stopwords: String, rest: String*): FingerprintAnalyzer = copy(stopwords = stopwords +: rest)
  def maxOutputSize(maxOutputSize: Int): FingerprintAnalyzer = copy(maxOutputSize = maxOutputSize)
}

case class PatternAnalyzer(override val name: String,
                           regex: String,
                           lowercase: Boolean = true) extends Analyzer {
  def build: XContentBuilder = {
    val b = XContentFactory.jsonBuilder()
    b.field("type", "pattern")
    b.field("lowercase", lowercase)
    b.field("pattern", regex)
    b.endObject()
  }

  def lowercase(lowercase: Boolean): PatternAnalyzer = copy(lowercase = lowercase)
}




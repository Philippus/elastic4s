package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.XContentBuilder

/** @author Stephen Samuel */
class Analyzer(val name: String)
case object NotAnalyzed extends Analyzer("notindexed")
case object WhitespaceAnalyzer extends Analyzer("whitespace")
case object StandardAnalyzer extends Analyzer("standard")
case object SimpleAnalyzer extends Analyzer("simple")
case object StopAnalyzer extends Analyzer("stop")
case object KeywordAnalyzer extends Analyzer("keyword")
case object PatternAnalyzer extends Analyzer("pattern")
case object SnowballAnalyzer extends Analyzer("snowball")

abstract class LanguageAnalyzer(name: String) extends Analyzer(name: String)

case object ArabicLanguageAnalyzer extends LanguageAnalyzer("arabic")
case object ArmenianLanguageAnalyzer extends LanguageAnalyzer("armenian")
case object BasqueLanguageAnalyzer extends LanguageAnalyzer("basque")
case object BrazilianLanguageAnalyzer extends LanguageAnalyzer("brazilian")
case object BulgarianLanguageAnalyzer extends LanguageAnalyzer("bulgarian")
case object CatalanLanguageAnalyzer extends LanguageAnalyzer("catalan")
case object ChineseLanguageAnalyzer extends LanguageAnalyzer("chinese")
case object CjkLanguageAnalyzer extends LanguageAnalyzer("cjk")
case object CzechLanguageAnalyzer extends LanguageAnalyzer("czech")
case object DanishLanguageAnalyzer extends LanguageAnalyzer("danish")
case object DutchLanguageAnalyzer extends LanguageAnalyzer("dutch")
case object EnglishLanguageAnalyzer extends LanguageAnalyzer("english")
case object FinnishLanguageAnalyzer extends LanguageAnalyzer("finnish")
case object FrenchLanguageAnalyzer extends LanguageAnalyzer("french")
case object GalicianLanguageAnalyzer extends LanguageAnalyzer("galician")
case object GermanLanguageAnalyzer extends LanguageAnalyzer("german")
case object GreekLanguageAnalyzer extends LanguageAnalyzer("greek")
case object HindiLanguageAnalyzer extends LanguageAnalyzer("hindi")
case object HungarianLanguageAnalyzer extends LanguageAnalyzer("hungarian")
case object IndonesianLanguageAnalyzer
  extends LanguageAnalyzer("indonesian")
case object ItalianLanguageAnalyzer extends LanguageAnalyzer("italian")
case object LatvianLanguageAnalyzer extends LanguageAnalyzer("latvian")
case object NorwegianLanguageAnalyzer extends LanguageAnalyzer("norwegian")
case object PersianLanguageAnalyzer extends LanguageAnalyzer("persian")
case object PortugueseLanguageAnalyzer
  extends LanguageAnalyzer("portuguese")
case object RomanianLanguageAnalyzer extends LanguageAnalyzer("romanian")
case object RussianLanguageAnalyzer extends LanguageAnalyzer("russian")
case object SpanishLanguageAnalyzer extends LanguageAnalyzer("spanish")
case object SwedishLanguageAnalyzer extends LanguageAnalyzer("swedish")
case object TurkishLanguageAnalyzer extends LanguageAnalyzer("turkish")
case object ThaiLanguageAnalyzer extends LanguageAnalyzer("thai")

abstract class AnalyzerDefinition(val name: String) {
  def build(source: XContentBuilder): Unit
}

case class StandardAnalyzerDefinition(override val name: String,
                                      stopwords: Iterable[String] = Nil,
                                      maxTokenLength: Int = 0) extends AnalyzerDefinition(name) {
  def build(source: XContentBuilder): Unit = {}
}

case class PatternAnalyzerDefinition(override val name: String,
                                     regex: String = null,
                                     lowercase: Boolean = true) extends Analyzer(name) {
  def build(source: XContentBuilder): Unit = {}
}

case class SnowballAnalyzerDefinition(override val name: String,
                                      lang: String = "english",
                                      stopwords: Iterable[String] = Nil) extends Analyzer(name) {
  def build(source: XContentBuilder): Unit = {}
}

case class CustomAnalyzerDefinition(override val name: String,
                                    tokenizer: String,
                                    filters: Iterable[String]) extends AnalyzerDefinition(name) {
  def this(name: String, tokenizer: String) = this(name, tokenizer, Nil)
  def this(name: String, filters: Iterable[String]) = this(name, null, filters)
  def build(source: XContentBuilder): Unit = {}
}

abstract class LanguageAnalyzerDef(name: String, stopwords: Iterable[String] = Nil) extends Analyzer(name) {
  def build(source: XContentBuilder): Unit = {
    source.startObject(name)
    source.field("lang", name)
    source.endObject()
  }
}

case class ArabicLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("arabic", stopwords)
case class ArmenianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("armenian", stopwords)
case class BasqueLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("basque", stopwords)
case class BrazilianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("brazilian", stopwords)
case class BulgarianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("bulgarian", stopwords)
case class CatalanLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("catalan", stopwords)
case class ChineseLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("chinese", stopwords)
case class CjkLanguageAnalyzerDef(stopwords: Iterable[String] = Nil) extends LanguageAnalyzerDef("cjk", stopwords)
case class CzechLanguageAnalyzerDef(stopwords: Iterable[String] = Nil) extends LanguageAnalyzerDef("czech", stopwords)
case class DanishLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("danish", stopwords)
case class DutchLanguageAnalyzerDef(stopwords: Iterable[String] = Nil) extends LanguageAnalyzerDef("dutch", stopwords)
case class EnglishLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("english", stopwords)
case class FinnishLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("finnish", stopwords)
case class FrenchLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("french", stopwords)
case class GalicianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("galician", stopwords)
case class GermanLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("german", stopwords)
case class GreekLanguageAnalyzerDef(stopwords: Iterable[String] = Nil) extends LanguageAnalyzerDef("greek", stopwords)
case class HindiLanguageAnalyzerDef(stopwords: Iterable[String] = Nil) extends LanguageAnalyzerDef("hindi", stopwords)
case class HungarianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("hungarian", stopwords)
case class IndonesianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("indonesian", stopwords)
case class ItalianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("italian", stopwords)
case class LatvianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("latvian", stopwords)
case class NorwegianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("norwegian", stopwords)
case class PersianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("persian", stopwords)
case class PortugueseLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("portuguese", stopwords)
case class RomanianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("romanian", stopwords)
case class RussianLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("russian", stopwords)
case class SpanishLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("spanish", stopwords)
case class SwedishLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("swedish", stopwords)
case class TurkishLanguageAnalyzerDef(stopwords: Iterable[String] = Nil)
  extends LanguageAnalyzerDef("turkish", stopwords)
case class ThaiLanguageAnalyzerDef(stopwords: Iterable[String] = Nil) extends LanguageAnalyzerDef("thai", stopwords)

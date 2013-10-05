package com.sksamuel.elastic4s

/** @author Stephen Samuel */
abstract class Analyzer(name: String)

case class NotAnalyzed extends Analyzer("notindexed")
case class WhitespaceAnalyzer extends Analyzer("whitespace")
case class StandardAnalyzer extends Analyzer("standard") {
  def stopwords(stopwords: Iterable[String]) = this
  def maxTokenLength(max: Int) = this
}
case class SimpleAnalyzer extends Analyzer("simple")
case class StopAnalyzer extends Analyzer("stop")
case class KeywordAnalyzer extends Analyzer("keyword")
case class PatternAnalyzer extends Analyzer("pattern") {
  def pattern(regex: String) = this
  def lowercase(lowercase: Boolean) = this
}
case class SnowballAnalyzer extends Analyzer("snowball") {
  def stopwords(stopwords: Iterable[String]) = this
  def language(lang: String) = this
}

abstract class LanguageAnalyzer(name: String) extends Analyzer(name) {
  def stopwords(stopwords: Iterable[String]) = this
}

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
case object IndonesianLanguageAnalyzer extends LanguageAnalyzer("indonesian")
case object ItalianLanguageAnalyzer extends LanguageAnalyzer("italian")
case object LatvianLanguageAnalyzer extends LanguageAnalyzer("latvian")
case object NorwegianLanguageAnalyzer extends LanguageAnalyzer("norwegian")
case object PersianLanguageAnalyzer extends LanguageAnalyzer("persian")
case object PortugueseLanguageAnalyzer extends LanguageAnalyzer("portuguese")
case object RomanianLanguageAnalyzer extends LanguageAnalyzer("romanian")
case object RussianLanguageAnalyzer extends LanguageAnalyzer("russian")
case object SpanishLanguageAnalyzer extends LanguageAnalyzer("spanish")
case object SwedishLanguageAnalyzer extends LanguageAnalyzer("swedish")
case object TurkishLanguageAnalyzer extends LanguageAnalyzer("turkish")
case object ThaiLanguageAnalyzer extends LanguageAnalyzer("thai")
